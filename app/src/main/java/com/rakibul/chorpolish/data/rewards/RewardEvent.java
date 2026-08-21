package com.rakibul.chorpolish.data.rewards;

/**
 * Encapsulates data related to a trigger that could lead to a reward.
 */
public class RewardEvent {
    private final RewardSource source;
    private final RewardType type;
    private final String metadata;

    public RewardEvent(RewardSource source, RewardType type, String metadata) {
        this.source = source;
        this.type = type;
        this.metadata = metadata;
    }

    public RewardSource getSource() { return source; }
    public RewardType getType() { return type; }
    public String getMetadata() { return metadata; }
}
