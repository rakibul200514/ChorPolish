package com.example.chorpolish.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorpolish.data.local.entity.GameRound
import com.example.chorpolish.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryUiState(
    val rounds: List<GameRound> = emptyList(),
    val stats: List<PlayerStat> = emptyList()
)

data class PlayerStat(
    val name: String,
    val totalScore: Int,
    val gamesPlayed: Int
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.getAllRounds()
        .map { rounds ->
            val statsMap = mutableMapOf<String, Int>()
            val gamesMap = mutableMapOf<String, Int>()

            rounds.forEach { round ->
                val players = listOf(
                    round.player1Name to round.player1Score,
                    round.player2Name to round.player2Score,
                    round.player3Name to round.player3Score,
                    round.player4Name to round.player4Score
                )
                players.forEach { (name, score) ->
                    statsMap[name] = (statsMap[name] ?: 0) + score
                    gamesMap[name] = (gamesMap[name] ?: 0) + 1
                }
            }

            val statsList = statsMap.map { (name, score) ->
                PlayerStat(name, score, gamesMap[name] ?: 0)
            }.sortedByDescending { it.totalScore }

            HistoryUiState(rounds = rounds, stats = statsList)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState()
        )

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
