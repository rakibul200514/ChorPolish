package com.rakibul.chorpolish.data.rewards;

import java.util.Collections;
import java.util.List;

/**
 * Isolated manager responsible for coordinating reward-related operations.
 * This class acts as the central hub for the Orbixa Rewards system foundation.
 */
public class RewardManager {
    private final RewardRepository repository;

    public RewardManager(RewardRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves current points from the repository.
     */
    public int getCurrentPoints() {
        if (repository == null) return 0;
        return repository.getCurrentPoints();
    }

    /**
     * Retrieves reward history from the repository.
     */
    public List<RewardTransaction> getRewardHistory() {
        if (repository == null) return Collections.emptyList();
        return repository.getRewardHistory();
    }

    /**
     * Isolated logic for calculating reward amounts based on event parameters.
     * Note: This does not award points yet.
     */
    public int calculateReward(RewardEvent event) {
        if (event == null) return 0;
        
        switch (event.getType()) {
            case LEVEL_COMPLETION:
                return RewardConfig.POINTS_LEVEL_COMPLETION;
            case REFERRAL:
                return RewardConfig.POINTS_REFERRAL;
            case DAILY_REWARD:
                return RewardConfig.POINTS_DAILY_REWARD;
            case ACHIEVEMENT:
                return RewardConfig.POINTS_ACHIEVEMENT_BASE;
            case BONUS:
                return RewardConfig.POINTS_BONUS_BASE;
            default:
                return 0;
        }
    }
}
