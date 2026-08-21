package com.rakibul.chorpolish.localgames.models;

import java.util.List;

public class LevelData {
    private final int levelNumber;
    private final List<int[]> tubeConfigurations;
    private final int totalTubes;
    private final String difficulty;

    public LevelData(int levelNumber, String difficulty, int totalTubes, List<int[]> tubeConfigurations) {
        this.levelNumber = levelNumber;
        this.difficulty = difficulty;
        this.totalTubes = totalTubes;
        this.tubeConfigurations = tubeConfigurations;
    }

    public int getLevelNumber() { return levelNumber; }
    public String getDifficulty() { return difficulty; }
    public int getTotalTubes() { return totalTubes; }
    public List<int[]> getTubeConfigurations() { return tubeConfigurations; }
    
    public int getRewardCoins() {
        switch (difficulty) {
            case "Medium": return 75;
            case "Hard": return 100;
            case "Expert": return 150;
            case "BOSS": return 300;
            default: return 50;
        }
    }
}