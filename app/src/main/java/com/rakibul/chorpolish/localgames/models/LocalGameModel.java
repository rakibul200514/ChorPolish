package com.rakibul.chorpolish.localgames.models;

public class LocalGameModel {
    private final String title;
    private final String subtitle;
    private final String difficulty;
    private final String status;
    private final int iconRes;
    private final boolean isLocked;

    public LocalGameModel(String title, String subtitle, String difficulty, String status, int iconRes, boolean isLocked) {
        this.title = title;
        this.subtitle = subtitle;
        this.difficulty = difficulty;
        this.status = status;
        this.iconRes = iconRes;
        this.isLocked = isLocked;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDifficulty() { return difficulty; }
    public String getStatus() { return status; }
    public int getIconRes() { return iconRes; }
    public boolean isLocked() { return isLocked; }
}