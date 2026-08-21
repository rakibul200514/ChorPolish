package com.rakibul.chorpolish.localgames.models;

public class Achievement {
    private final String id;
    private final String title;
    private final String description;
    private final int targetValue;
    private int currentValue;
    private boolean isUnlocked;

    public Achievement(String id, String title, String description, int targetValue) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetValue = targetValue;
        this.currentValue = 0;
        this.isUnlocked = false;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTargetValue() { return targetValue; }
    public int getCurrentValue() { return currentValue; }
    public boolean isUnlocked() { return isUnlocked; }

    public boolean addProgress(int amount) {
        if (isUnlocked) return false;
        currentValue += amount;
        if (currentValue >= targetValue) {
            isUnlocked = true;
            return true;
        }
        return false;
    }
}