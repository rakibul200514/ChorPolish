package com.rakibul.chorpolish.data.rewards;

import java.util.UUID;

/**
 * Model representing a single reward transaction history entry.
 */
public class RewardTransaction {
    private final String id;
    private final RewardType type;
    private final RewardSource source;
    private final int points;
    private final long timestamp;
    private final String gameName;
    private final Integer levelNumber;

    public RewardTransaction(RewardType type, RewardSource source, int points, String gameName, Integer levelNumber) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.source = source;
        this.points = points;
        this.timestamp = System.currentTimeMillis();
        this.gameName = gameName;
        this.levelNumber = levelNumber;
    }

    // Getters
    public String getId() { return id; }
    public RewardType getType() { return type; }
    public RewardSource getSource() { return source; }
    public int getPoints() { return points; }
    public long getTimestamp() { return timestamp; }
    public String getGameName() { return gameName; }
    public Integer getLevelNumber() { return levelNumber; }
}
