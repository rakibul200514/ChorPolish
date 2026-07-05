package com.rakibul.chorpolish.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakibul.chorpolish.data.local.entity.GameRound
import com.rakibul.chorpolish.data.local.SessionManager
import com.rakibul.chorpolish.data.model.GameRole
import com.rakibul.chorpolish.data.model.Player
import com.rakibul.chorpolish.data.repository.GameRepository
import com.rakibul.chorpolish.utils.SoundEffect
import com.rakibul.chorpolish.utils.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class GameMode {
    CHOR_POLISH,
    LETTER_SET
}

data class GameUiState(
    val players: List<Player> = listOf(
        Player(0, "You", isRobot = false),
        Player(1, "Robot 1", isRobot = true),
        Player(2, "Robot 2", isRobot = true),
        Player(3, "Robot 3", isRobot = true)
    ),
    val currentRound: Int = 1,
    val phase: GamePhase = GamePhase.SETUP,
    val gameMode: GameMode = GameMode.CHOR_POLISH,
    val mantriPlayer: Player? = null,
    val rajaPlayer: Player? = null,
    val chorPlayer: Player? = null,
    val sipahiPlayer: Player? = null,
    val guessOptions: List<Player> = emptyList(),
    val lastPassedCard: String? = null, // Store last passed card for Letter Set visibility
    val selectedChorGuess: Player? = null,
    val isGuessCorrect: Boolean? = null,
    val roundResults: List<PlayerScore> = emptyList(),
    val showRules: Boolean = false,
    val playerCards: Map<Int, List<String>> = emptyMap(), // Player ID to list of letters
    val winner: Player? = null,
    val currentPlayerTurn: Int = 0, // For Letter Set mode
    val maxRounds: Int = 8
)

enum class GamePhase {
    SETUP,
    ROLE_REVEAL,
    GUESSING,
    RESULT,
    PLAYING // New phase for Letter Set gameplay
}

data class PlayerScore(
    val playerName: String,
    val role: GameRole,
    val score: Int
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository,
    private val sessionManager: SessionManager,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.rulesSeen.collect { seen ->
                _uiState.update { it.copy(showRules = !seen) }
            }
        }
    }

    fun dismissRules() {
        viewModelScope.launch {
            sessionManager.setRulesSeen(true)
        }
    }

    fun updatePlayerName(index: Int, name: String) {
        _uiState.update { state ->
            val newPlayers = state.players.toMutableList()
            newPlayers[index] = newPlayers[index].copy(name = name)
            state.copy(players = newPlayers)
        }
    }

    fun setGameMode(mode: GameMode) {
        _uiState.update { it.copy(gameMode = mode) }
    }

    fun setMaxRounds(rounds: Int) {
        _uiState.update { it.copy(maxRounds = rounds) }
    }

    fun startGame() {
        soundManager.playSoundEffect(SoundEffect.BUTTON_CLICK)
        if (_uiState.value.gameMode == GameMode.CHOR_POLISH) {
            startNewRound()
        } else {
            startLetterSetGame()
        }
    }

    private fun startLetterSetGame() {
        val deck = mutableListOf<String>()
        repeat(4) { deck.add("A"); deck.add("B"); deck.add("C"); deck.add("D") }
        
        var validDistribution = false
        val cards = mutableMapOf<Int, List<String>>()
        
        while (!validDistribution) {
            deck.shuffle()
            cards.clear()
            validDistribution = true
            
            for (i in 0 until 4) {
                val hand = deck.subList(i * 4, (i + 1) * 4)
                // If any player has 4 of the same card, it's an invalid initial distribution
                if (hand.all { it == hand[0] }) {
                    validDistribution = false
                    break
                }
            }
        }

        _uiState.update { state ->
            val finalCards = mutableMapOf<Int, List<String>>()
            val resetPlayers = state.players.map { it.copy(hasSeenRole = false) }
            resetPlayers.forEachIndexed { index, player ->
                finalCards[player.id] = deck.subList(index * 4, (index + 1) * 4).toList()
            }

            state.copy(
                players = resetPlayers,
                phase = GamePhase.ROLE_REVEAL, // Reuse reveal to show initial cards
                playerCards = finalCards,
                lastPassedCard = null,
                winner = null,
                currentPlayerTurn = 0,
                isGuessCorrect = null,
                roundResults = emptyList()
            )
        }
    }

    fun passCard(fromPlayerId: Int, toPlayerId: Int, card: String) {
        soundManager.playSoundEffect(SoundEffect.CARD_FLIP)
        _uiState.update { state ->
            val fromCards = state.playerCards[fromPlayerId]?.toMutableList() ?: mutableListOf()
            val toCards = state.playerCards[toPlayerId]?.toMutableList() ?: mutableListOf()
            
            fromCards.remove(card)
            toCards.add(card)

            val newPlayerCards = state.playerCards.toMutableMap()
            newPlayerCards[fromPlayerId] = fromCards
            newPlayerCards[toPlayerId] = toCards

            // Check for winner
            var winner: Player? = null
            var roundScores = emptyList<PlayerScore>()
            
            if (toCards.size == 4 && toCards.distinct().size == 1) {
                winner = state.players.find { it.id == toPlayerId }
                soundManager.playSoundEffect(SoundEffect.WINNER_SOUND)
                
                // Assign scores for Letter Set
                roundScores = state.players.map { player ->
                    val score = if (player.id == toPlayerId) 100 else 0
                    PlayerScore(player.name, GameRole.SIPAHI, score) // Default role for scoring
                }
                
                // Save to history
                saveLetterSetRoundToDb(state.copy(playerCards = newPlayerCards, winner = winner), roundScores)
            }

            // Next player index
            val nextPlayerIndex = (state.players.indexOfFirst { it.id == fromPlayerId } + 1) % 4

            state.copy(
                playerCards = newPlayerCards,
                lastPassedCard = card,
                winner = winner,
                phase = if (winner != null) GamePhase.RESULT else state.phase,
                currentPlayerTurn = nextPlayerIndex,
                roundResults = roundScores,
                players = state.players.map { p ->
                    val addedScore = roundScores.find { it.playerName == p.name }?.score ?: 0
                    p.copy(sessionScore = p.sessionScore + addedScore)
                }
            )
        }

        // Robot Turn Logic for Letter Set
        val currentState = _uiState.value
        if (currentState.gameMode == GameMode.LETTER_SET && currentState.phase == GamePhase.PLAYING && currentState.winner == null) {
            val activePlayer = currentState.players[currentState.currentPlayerTurn]
            if (activePlayer.isRobot) {
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000) // "Thinking" and visibility delay
                    
                    val robotCards = currentState.playerCards[activePlayer.id] ?: emptyList()
                    if (robotCards.isNotEmpty()) {
                        // Robot Strategy: Pass a card that doesn't have a pair, or just a random one.
                        // Count occurrences of each letter
                        val counts = robotCards.groupingBy { it }.eachCount()
                        // Find a letter that has the minimum count (least likely to form a set soon)
                        val cardToPass = counts.minByOrNull { it.value }?.key ?: robotCards.random()
                        
                        val nextPlayerId = currentState.players[(currentState.currentPlayerTurn + 1) % 4].id
                        passCard(activePlayer.id, nextPlayerId, cardToPass)
                    }
                }
            }
        }
    }

    private fun saveLetterSetRoundToDb(state: GameUiState, results: List<PlayerScore>) {
        viewModelScope.launch {
            val round = GameRound(
                player1Name = state.players[0].name,
                player2Name = state.players[1].name,
                player3Name = state.players[2].name,
                player4Name = state.players[3].name,
                player1Role = GameRole.SIPAHI, // Placeholders for Letter Set
                player2Role = GameRole.SIPAHI,
                player3Role = GameRole.SIPAHI,
                player4Role = GameRole.SIPAHI,
                player1Score = results.find { it.playerName == state.players[0].name }?.score ?: 0,
                player2Score = results.find { it.playerName == state.players[1].name }?.score ?: 0,
                player3Score = results.find { it.playerName == state.players[2].name }?.score ?: 0,
                player4Score = results.find { it.playerName == state.players[3].name }?.score ?: 0,
                isGuessCorrect = true,
                chorPlayerName = "LETTER_SET",
                mantriPlayerName = state.winner?.name ?: ""
            )
            repository.insertRound(round)
        }
    }

    fun startNewRound() {
        if (_uiState.value.gameMode == GameMode.LETTER_SET) {
            startLetterSetGame()
            return
        }

        val roles = GameRole.getAllRoles().shuffled()
        _uiState.update { state ->
            val updatedPlayers = state.players.mapIndexed { index, player ->
                player.copy(currentRole = roles[index], hasSeenRole = false)
            }
            
            val raja = updatedPlayers.find { it.currentRole == GameRole.RAJA }
            val mantri = updatedPlayers.find { it.currentRole == GameRole.MANTRI }
            val chor = updatedPlayers.find { it.currentRole == GameRole.CHOR }
            val sipahi = updatedPlayers.find { it.currentRole == GameRole.SIPAHI }

            state.copy(
                players = updatedPlayers,
                phase = GamePhase.ROLE_REVEAL,
                rajaPlayer = raja,
                mantriPlayer = mantri,
                chorPlayer = chor,
                sipahiPlayer = sipahi,
                selectedChorGuess = null,
                isGuessCorrect = null,
                roundResults = emptyList(),
                guessOptions = emptyList(),
                currentRound = if (state.phase == GamePhase.RESULT) state.currentRound + 1 else state.currentRound
            )
        }
    }

    fun playerSawRole(playerId: Int) {
        _uiState.update { state ->
            val updatedPlayers = state.players.map { player ->
                if (player.id == playerId) player.copy(hasSeenRole = true) else player
            }
            
            val allSeen = updatedPlayers.all { it.hasSeenRole }
            if (allSeen) {
                if (state.gameMode == GameMode.CHOR_POLISH) {
                    // Determine guess options: Everyone except Police (King, Minister, Thief)
                    val guessOptions = updatedPlayers.filter {
                        it.currentRole != null && it.currentRole != GameRole.SIPAHI
                    }.shuffled()

                    state.copy(
                        players = updatedPlayers,
                        phase = GamePhase.GUESSING,
                        guessOptions = guessOptions
                    )
                } else {
                    state.copy(
                        players = updatedPlayers,
                        phase = GamePhase.PLAYING
                    )
                }
            } else {
                state.copy(players = updatedPlayers)
            }
        }

        val currentState = _uiState.value
        
        // Auto-progress for Robots in Reveal Phase
        if (currentState.phase == GamePhase.ROLE_REVEAL) {
            val nextPlayer = currentState.players.find { !it.hasSeenRole }
            if (nextPlayer != null && nextPlayer.isRobot) {
                viewModelScope.launch {
                    val humanPlayer = currentState.players.find { !it.isRobot }
                    val isHumanPolice = humanPlayer?.currentRole == GameRole.SIPAHI
                    
                    // Delay to let human see the robot's role (unless human is Police)
                    val delayTime = if (isHumanPolice) 1200L else 3000L
                    kotlinx.coroutines.delay(delayTime)
                    playerSawRole(nextPlayer.id)
                }
            }
        }

        // Robot Guessing Logic
        if (currentState.phase == GamePhase.GUESSING && currentState.sipahiPlayer?.isRobot == true) {
            viewModelScope.launch {
                kotlinx.coroutines.delay(2000) // Thinking time
                val suspects = currentState.guessOptions
                val randomGuess = suspects.random()
                makeGuess(randomGuess)
            }
        }
    }

    fun makeGuess(guessedChor: Player) {
        _uiState.update { state ->
            val isCorrect = guessedChor.currentRole == GameRole.CHOR
            val results = calculateScores(state.players, isCorrect)
            
            soundManager.playSoundEffect(if (isCorrect) SoundEffect.GUESS_CORRECT else SoundEffect.GUESS_WRONG)
            
            // Update session scores
            val updatedPlayers = state.players.map { player ->
                val roundScore = results.find { it.playerName == player.name }?.score ?: 0
                player.copy(sessionScore = player.sessionScore + roundScore)
            }

            // Save to database
            saveRoundToDb(state, isCorrect, results)

            state.copy(
                players = updatedPlayers,
                phase = GamePhase.RESULT,
                selectedChorGuess = guessedChor,
                isGuessCorrect = isCorrect,
                roundResults = results
            )
        }
    }

    private fun calculateScores(players: List<Player>, isGuessCorrect: Boolean): List<PlayerScore> {
        return players.map { player ->
            val score = when (player.currentRole) {
                GameRole.RAJA -> 1000
                GameRole.MANTRI -> 800
                GameRole.SIPAHI -> if (isGuessCorrect) 500 else 0
                GameRole.CHOR -> if (isGuessCorrect) 0 else 500
                null -> 0
            }
            PlayerScore(player.name, player.currentRole ?: GameRole.CHOR, score)
        }
    }

    private fun saveRoundToDb(state: GameUiState, isCorrect: Boolean, results: List<PlayerScore>) {
        viewModelScope.launch {
            val round = GameRound(
                player1Name = state.players[0].name,
                player2Name = state.players[1].name,
                player3Name = state.players[2].name,
                player4Name = state.players[3].name,
                player1Role = state.players[0].currentRole!!,
                player2Role = state.players[1].currentRole!!,
                player3Role = state.players[2].currentRole!!,
                player4Role = state.players[3].currentRole!!,
                player1Score = results.find { it.playerName == state.players[0].name }?.score ?: 0,
                player2Score = results.find { it.playerName == state.players[1].name }?.score ?: 0,
                player3Score = results.find { it.playerName == state.players[2].name }?.score ?: 0,
                player4Score = results.find { it.playerName == state.players[3].name }?.score ?: 0,
                isGuessCorrect = isCorrect,
                chorPlayerName = state.chorPlayer?.name ?: "",
                mantriPlayerName = state.mantriPlayer?.name ?: ""
            )
            repository.insertRound(round)
        }
    }

    fun playRoleAnnouncement(role: GameRole) {
        val roleName = when(role) {
            GameRole.RAJA -> "King"
            GameRole.MANTRI -> "Minister"
            GameRole.SIPAHI -> "Police"
            GameRole.CHOR -> "Thief"
        }
        soundManager.speakRoleAnnouncement(roleName)
    }

    fun playSoundEffect(effect: SoundEffect) {
        soundManager.playSoundEffect(effect)
    }

    fun resetGame() {
        _uiState.update { 
            GameUiState(
                players = listOf(
                    Player(0, it.players[0].name, isRobot = false),
                    Player(1, "Robot 1", isRobot = true),
                    Player(2, "Robot 2", isRobot = true),
                    Player(3, "Robot 3", isRobot = true)
                ),
                currentRound = 1,
                phase = GamePhase.SETUP
            )
        }
    }
}
