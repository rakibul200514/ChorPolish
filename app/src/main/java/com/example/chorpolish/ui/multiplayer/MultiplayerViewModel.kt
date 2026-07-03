package com.example.chorpolish.ui.multiplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorpolish.data.local.SessionManager
import com.example.chorpolish.data.model.GameRole
import com.example.chorpolish.data.local.entity.GameRound
import com.example.chorpolish.data.repository.GameRepository
import com.example.chorpolish.data.remote.MultiplayerEvent
import com.example.chorpolish.data.remote.MultiplayerRepository
import com.example.chorpolish.data.remote.model.RoomResponse
import com.example.chorpolish.ui.navigation.NavDestination
import com.example.chorpolish.utils.SoundEffect
import com.example.chorpolish.utils.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MultiplayerUiState(
    val roomCode: String? = null,
    val players: List<String> = emptyList(),
    val playerIds: List<String> = emptyList(), // Added playerIds
    val isHost: Boolean = false,
    val myName: String = "",
    val myPlayerId: String = "",
    val myRole: GameRole? = null,
    val isReconnecting: Boolean = false,
    val phase: String = "LOBBY",
    val gameMode: String = "CHOR_POLISH",
    val mantriPlayer: String? = null,
    val rajaPlayer: String? = null,
    val chorPlayer: String? = null,
    val sipahiPlayer: String? = null,
    val guessOptions: List<String> = emptyList(),
    val isGuessCorrect: Boolean? = null,
    val roundScores: List<Pair<String, Int>> = emptyList(),
    val totalScores: List<Pair<String, Int>> = emptyList(),
    val roundScoresWithRoles: List<Triple<String, Int, GameRole?>> = emptyList(),
    val currentRound: Int = 1,
    val error: String? = null,
    val activeReactions: List<EmojiReaction> = emptyList(),
    val playerCards: Map<String, List<String>> = emptyMap(),
    val winner: String? = null,
    val currentPlayerTurn: Int = 0,
    val premiumEmojisUnlocked: Boolean = false,
    val maxRounds: Int = 8
)

data class EmojiReaction(
    val playerName: String,
    val emoji: String,
    val id: Long = System.currentTimeMillis() + (0..1000).random()
)

@HiltViewModel
class MultiplayerViewModel @Inject constructor(
    private val repository: MultiplayerRepository,
    private val sessionManager: SessionManager,
    private val gameRepository: GameRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiplayerUiState())
    val uiState: StateFlow<MultiplayerUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavDestination>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            var pId = sessionManager.playerId.first()
            if (pId.isEmpty()) {
                pId = java.util.UUID.randomUUID().toString()
                sessionManager.savePlayerId(pId)
            }
            val savedName = sessionManager.playerName.first()
            _uiState.update { it.copy(myPlayerId = pId, myName = savedName) }
            
            repository.connect()
            repository.observeRoomEvents().collect { event ->
                handleMultiplayerEvent(event)
            }
        }

        viewModelScope.launch {
            val savedRoomCode = sessionManager.roomCode.first()
            val savedName = sessionManager.playerName.first()
            if (savedRoomCode != null && savedName != "Anonymous") {
                joinRoom(savedName, savedRoomCode)
            }
        }
        
        viewModelScope.launch {
            sessionManager.premiumEmojisUnlocked.collect { unlocked ->
                _uiState.update { it.copy(premiumEmojisUnlocked = unlocked) }
            }
        }
    }

    private fun handleMultiplayerEvent(event: MultiplayerEvent) {
        when (event) {
            is MultiplayerEvent.RoomUpdated -> updateStateFromRoom(event.room)
            is MultiplayerEvent.GameStarted -> updateStateFromRoom(event.room)
            is MultiplayerEvent.GuessResult -> {
                updateStateFromRoom(event.room)
                val isCorrect = event.room.isGuessCorrect ?: false
                soundManager.playSoundEffect(if (isCorrect) SoundEffect.GUESS_CORRECT else SoundEffect.GUESS_WRONG)
            }
            is MultiplayerEvent.GameFinished -> {
                android.util.Log.d("SocketIO", "Game finished event handled in ViewModel")
                updateStateFromRoom(event.room)
                saveCurrentRound()
                // Do NOT navigate to History immediately.
                // The UI (MultiplayerResultScreen) will handle the celebration and then navigate.
            }
            is MultiplayerEvent.EmojiReceived -> {
                val reaction = EmojiReaction(event.playerName, event.emoji)
                _uiState.update { it.copy(activeReactions = it.activeReactions + reaction) }

                // Remove emoji after 3 seconds
                viewModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    _uiState.update { state ->
                        state.copy(activeReactions = state.activeReactions.filter { it.id != reaction.id })
                    }
                }
            }
        }
    }

    private fun updateStateFromRoom(room: RoomResponse) {
        val currentMyPlayerId = _uiState.value.myPlayerId.ifEmpty { 
            // Fallback: try to find by name if ID is somehow lost in state
            room.players?.find { it.name == _uiState.value.myName }?.playerId ?: ""
        }
        
        val myRemotePlayer = room.players?.find { it.playerId == currentMyPlayerId }
        val myRole = when(myRemotePlayer?.role?.uppercase()) {
            "KING", "RAJA" -> GameRole.RAJA
            "MINISTER", "MANTRI" -> GameRole.MANTRI
            "POLICE", "SIPAHI" -> GameRole.SIPAHI
            "THIEF", "CHOR" -> GameRole.CHOR
            else -> null
        }

        val previousWinner = _uiState.value.winner
        val previousTurn = _uiState.value.currentPlayerTurn

        _uiState.update { state ->
            state.copy(
                roomCode = room.roomCode ?: state.roomCode,
                players = room.players?.mapNotNull { it.name } ?: state.players,
                playerIds = room.players?.mapNotNull { it.playerId } ?: state.playerIds,
                isHost = room.host == state.myName,
                phase = room.phase ?: "LOBBY",
                gameMode = room.gameMode ?: "CHOR_POLISH",
                myRole = myRole ?: state.myRole, // Keep existing role if not in update
                myPlayerId = if (state.myPlayerId.isEmpty()) currentMyPlayerId else state.myPlayerId,
                mantriPlayer = room.mantriPlayer,
                rajaPlayer = room.rajaPlayer,
                chorPlayer = room.chorPlayer,
                sipahiPlayer = room.sipahiPlayer,
                guessOptions = room.guessOptions ?: emptyList(),
                isGuessCorrect = room.isGuessCorrect,
                roundScores = room.players?.mapNotNull { it.name?.to(it.score ?: 0) } ?: emptyList(),
                totalScores = room.players?.mapNotNull { it.name?.to(it.score ?: 0) } ?: emptyList(),
                roundScoresWithRoles = room.players?.mapNotNull { p ->
                    val role = when(p.role?.uppercase()) {
                        "KING", "RAJA" -> GameRole.RAJA
                        "MINISTER", "MANTRI" -> GameRole.MANTRI
                        "POLICE", "SIPAHI" -> GameRole.SIPAHI
                        "THIEF", "CHOR" -> GameRole.CHOR
                        else -> null
                    }
                    if (p.name != null) Triple(p.name, p.score ?: 0, role) else null
                } ?: emptyList(),
                currentRound = room.currentRound ?: 1,
                maxRounds = room.maxRounds ?: state.maxRounds,
                playerCards = room.playerCards ?: emptyMap(),
                winner = room.winner,
                currentPlayerTurn = room.currentPlayerTurn ?: 0
            )
        }

        // Play sounds based on changes
        if (room.winner != null && previousWinner == null) {
            soundManager.playSoundEffect(SoundEffect.WINNER_SOUND)
        } else if (room.phase == "PLAYING" && room.currentPlayerTurn != previousTurn) {
            soundManager.playSoundEffect(SoundEffect.CARD_FLIP)
        }
    }

    fun createRoom(playerName: String, gameMode: String, maxRounds: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null, gameMode = gameMode, maxRounds = maxRounds) }
            var pId = _uiState.value.myPlayerId
            if (pId.isEmpty()) {
                pId = sessionManager.playerId.first().ifEmpty { 
                    java.util.UUID.randomUUID().toString().also { sessionManager.savePlayerId(it) }
                }
            }
            // Update createRoom call to pass maxRounds if API supports it
            val result = repository.createRoom(playerName, pId, gameMode, maxRounds)
            result.onSuccess { response ->
                _uiState.update { 
                    it.copy(
                        roomCode = response.roomCode,
                        players = response.players?.mapNotNull { p -> p.name } ?: emptyList(),
                        isHost = true,
                        myName = playerName,
                        myPlayerId = pId,
                        phase = response.phase ?: "LOBBY"
                    )
                }
                response.roomCode?.let { sessionManager.saveRoomCode(it) }
                sessionManager.savePlayerName(playerName)
                _navigationEvent.emit(NavDestination.MultiplayerLobby)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Failed to create room") }
            }
        }
    }

    fun joinRoom(playerName: String, roomCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            var pId = _uiState.value.myPlayerId
            if (pId.isEmpty()) {
                pId = sessionManager.playerId.first().ifEmpty { 
                    java.util.UUID.randomUUID().toString().also { sessionManager.savePlayerId(it) }
                }
            }
            val result = repository.joinRoom(playerName, pId, roomCode)
            result.onSuccess { response ->
                _uiState.update { 
                    it.copy(
                        roomCode = response.roomCode,
                        players = response.players?.mapNotNull { p -> p.name } ?: emptyList(),
                        isHost = response.host == playerName,
                        myName = playerName,
                        myPlayerId = pId,
                        phase = response.phase ?: "LOBBY"
                    )
                }
                response.roomCode?.let { sessionManager.saveRoomCode(it) }
                sessionManager.savePlayerName(playerName)
                _navigationEvent.emit(NavDestination.MultiplayerLobby)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Failed to join room") }
            }
        }
    }

    fun triggerStart(gameMode: String = "CHOR_POLISH") {
        soundManager.playSoundEffect(SoundEffect.BUTTON_CLICK)
        _uiState.value.roomCode?.let {
            repository.triggerStart(it, gameMode)
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

    fun roleSeen() {
        val roomCode = _uiState.value.roomCode ?: return
        repository.roleSeen(roomCode, _uiState.value.myPlayerId)
    }

    fun makeGuess(guessedPlayerName: String) {
        val roomCode = _uiState.value.roomCode ?: return
        repository.makeGuess(roomCode, guessedPlayerName)
    }

    fun passCard(toPlayerId: String, card: String) {
        val roomCode = _uiState.value.roomCode ?: return
        repository.passCard(roomCode, _uiState.value.myPlayerId, toPlayerId, card)
    }

    fun triggerNextRound() {
        _uiState.value.roomCode?.let {
            repository.triggerNextRound(it)
        }
    }

    fun triggerFinishGame() {
        android.util.Log.d("SocketIO", "triggerFinishGame called")
        _uiState.value.roomCode?.let {
            repository.triggerFinishGame(it)
        }
    }

    fun restartMatch() {
        _uiState.value.roomCode?.let {
            repository.triggerRestartMatch(it)
        }
    }

    fun sendEmoji(emoji: String) {
        val state = _uiState.value
        state.roomCode?.let {
            repository.sendEmoji(it, emoji, state.myName)
        }
    }

    fun saveCurrentRound() {
        val state = _uiState.value
        if (state.roundScoresWithRoles.size == 4) {
            viewModelScope.launch {
                val round = GameRound(
                    player1Name = state.roundScoresWithRoles[0].first,
                    player2Name = state.roundScoresWithRoles[1].first,
                    player3Name = state.roundScoresWithRoles[2].first,
                    player4Name = state.roundScoresWithRoles[3].first,
                    player1Role = state.roundScoresWithRoles[0].third ?: GameRole.SIPAHI,
                    player2Role = state.roundScoresWithRoles[1].third ?: GameRole.SIPAHI,
                    player3Role = state.roundScoresWithRoles[2].third ?: GameRole.SIPAHI,
                    player4Role = state.roundScoresWithRoles[3].third ?: GameRole.SIPAHI,
                    player1Score = state.roundScoresWithRoles[0].second,
                    player2Score = state.roundScoresWithRoles[1].second,
                    player3Score = state.roundScoresWithRoles[2].second,
                    player4Score = state.roundScoresWithRoles[3].second,
                    isGuessCorrect = if (state.gameMode == "LETTER_SET") true else (state.isGuessCorrect ?: false),
                    chorPlayerName = if (state.gameMode == "LETTER_SET") "LETTER_SET" else (state.chorPlayer ?: ""),
                    mantriPlayerName = if (state.gameMode == "LETTER_SET") (state.winner ?: "") else (state.mantriPlayer ?: "")
                )
                gameRepository.insertRound(round)
            }
        }
    }

    override fun onCleared() {
        repository.disconnect()
    }
}
