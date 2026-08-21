package com.rakibul.chorpolish.data.rewards;

import java.util.List;

/**
 * Interface for managing reward data persistence and retrieval.
 */
public interface RewardRepository {
    /**
     * Retrieves the total points accumulated by the user.
     */
    int getCurrentPoints();

    /**
     * Retrieves the history of all reward transactions.
     */
    List<RewardTransaction> getRewardHistory();

    /**
     * Saves a new reward transaction.
     */
    void saveTransaction(RewardTransaction transaction);
}
