package com.rakibul.chorpolish.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.rakibul.chorpolish.data.local.entity.GameRound
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRoundDao {
    @Insert
    suspend fun insertRound(round: GameRound)

    @Query("SELECT * FROM game_rounds ORDER BY timestamp DESC")
    fun getAllRounds(): Flow<List<GameRound>>

    @Query("SELECT SUM(player1Score) FROM game_rounds WHERE player1Name = :playerName")
    suspend fun getPlayer1TotalScore(playerName: String): Int?

    @Query("DELETE FROM game_rounds")
    suspend fun clearHistory()
}
