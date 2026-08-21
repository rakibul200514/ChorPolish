package com.rakibul.chorpolish.di

import android.content.Context
import com.rakibul.chorpolish.data.rewards.LocalRewardRepository
import com.rakibul.chorpolish.data.rewards.RewardManager
import com.rakibul.chorpolish.data.rewards.RewardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RewardModule {

    @Provides
    @Singleton
    fun provideRewardRepository(@ApplicationContext context: Context): RewardRepository {
        return LocalRewardRepository(context)
    }

    @Provides
    @Singleton
    fun provideRewardManager(repository: RewardRepository): RewardManager {
        return RewardManager(repository)
    }
}
