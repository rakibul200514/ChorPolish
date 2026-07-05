package com.rakibul.chorpolish.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rakibul.chorpolish.data.model.GameRole

@Entity(tableName = "game_rounds")
data class GameRound(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val player1Name: String,
    val player2Name: String,
    val player3Name: String,
    val player4Name: String,
    val player1Role: GameRole,
    val player2Role: GameRole,
    val player3Role: GameRole,
    val player4Role: GameRole,
    val player1Score: Int,
    val player2Score: Int,
    val player3Score: Int,
    val player4Score: Int,
    val isGuessCorrect: Boolean,
    val chorPlayerName: String,
    val mantriPlayerName: String
)
