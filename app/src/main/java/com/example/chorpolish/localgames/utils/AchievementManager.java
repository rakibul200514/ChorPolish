package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.rakibul.chorpolish.localgames.models.Achievement;
import java.util.ArrayList;
import java.util.List;

public class AchievementManager {
    private static final String PREF_NAME = "AchievementsPrefs";
    private final List<Achievement> achievements;
    private final SharedPreferences prefs;

    public AchievementManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        achievements = new ArrayList<>();
        initAchievements();
        loadProgress();
    }

    private void initAchievements() {
        achievements.add(new Achievement("first_win", "First Win", "Complete your first level", 1));
        achievements.add(new Achievement("moves_100", "100 Moves", "Make 100 moves total", 100));
        achievements.add(new Achievement("levels_50", "Halfway There", "Clear 50 levels", 50));
        achievements.add(new Achievement("coins_1000", "Wealthy", "Collect 1000 coins", 1000));
    }

    private void loadProgress() {
        for (Achievement a : achievements) {
            int progress = prefs.getInt(a.getId() + "_progress", 0);
            a.addProgress(progress);
        }
    }

    public void updateAchievement(String id, int amount) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id)) {
                if (a.addProgress(amount)) {
                    // Achievement unlocked!
                    saveProgress(a);
                }
                saveProgress(a);
                break;
            }
        }
    }

    private void saveProgress(Achievement a) {
        prefs.edit().putInt(a.getId() + "_progress", a.getCurrentValue()).apply();
        prefs.edit().putBoolean(a.getId() + "_unlocked", a.isUnlocked()).apply();
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}