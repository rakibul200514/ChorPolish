package com.example.chorpolish.data.repository

import com.example.chorpolish.data.local.dao.GameRoundDao
import com.example.chorpolish.data.local.entity.GameRound
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameRoundDao: GameRoundDao
) {
    fun getAllRounds(): Flow<List<GameRound>> = gameRoundDao.getAllRounds()

    suspend fun insertRound(round: GameRound) {
        gameRoundDao.insertRound(round)
    }

    suspend fun clearHistory() {
        gameRoundDao.clearHistory()
    }
}
