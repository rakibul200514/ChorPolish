package com.example.chorpolish.localgames.models;

public class LocalGameModel {
    private String title;
    private String subtitle;
    private String difficulty;
    private String status;
    private int iconRes;
    private boolean isLocked;

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