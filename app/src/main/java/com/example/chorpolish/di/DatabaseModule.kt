package com.example.chorpolish.di

import android.content.Context
import androidx.room.Room
import com.example.chorpolish.data.local.AppDatabase
import com.example.chorpolish.data.local.dao.GameRoundDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideGameRoundDao(database: AppDatabase): GameRoundDao {
        return database.gameRoundDao()
    }
}
