package com.example.chorpolish.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chorpolish.data.local.dao.GameRoundDao
import com.example.chorpolish.data.local.entity.GameRound

@Database(entities = [GameRound::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameRoundDao(): GameRoundDao

    companion object {
        const val DATABASE_NAME = "chor_polish_db"
    }
}
