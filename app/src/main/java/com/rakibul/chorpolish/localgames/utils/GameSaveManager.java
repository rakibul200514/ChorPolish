package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GameSaveManager {
    private static final String PREF_NAME = "NutSortPuzzleSave";
    private static final String KEY_LATEST_LEVEL = "latest_level";
    private static final String KEY_UNLOCKED_LEVEL = "unlocked_level";
    private static final String KEY_COINS = "coins";
    private static final String KEY_SOUND = "sound_on";
    private static final String KEY_MUSIC = "music_on";
    private static final String KEY_VIBRATION = "vibration_on";

    private final SharedPreferences prefs;

    public GameSaveManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getLatestLevel() { return prefs.getInt(KEY_LATEST_LEVEL, 1); }
    public void setLatestLevel(int level) { prefs.edit().putInt(KEY_LATEST_LEVEL, level).apply(); }

    public int getUnlockedLevel() { return prefs.getInt(KEY_UNLOCKED_LEVEL, 1); }
    public void unlockLevel(int level) {
        if (level > getUnlockedLevel()) {
            prefs.edit().putInt(KEY_UNLOCKED_LEVEL, level).apply();
        }
    }

    public int getCoins() { return prefs.getInt(KEY_COINS, 500); }
    public void addCoins(int amount) { prefs.edit().putInt(KEY_COINS, getCoins() + amount).apply(); }
    public boolean spendCoins(int amount) {
        int current = getCoins();
        if (current >= amount) {
            prefs.edit().putInt(KEY_COINS, current - amount).apply();
            return true;
        }
        return false;
    }

    public boolean isSoundOn() { return prefs.getBoolean(KEY_SOUND, true); }
    public void setSoundOn(boolean on) { prefs.edit().putBoolean(KEY_SOUND, on).apply(); }

    public boolean isMusicOn() { return prefs.getBoolean(KEY_MUSIC, true); }
    public void setMusicOn(boolean on) { prefs.edit().putBoolean(KEY_MUSIC, on).apply(); }

    public boolean isVibrationOn() { return prefs.getBoolean(KEY_VIBRATION, true); }
    public void setVibrationOn(boolean on) { prefs.edit().putBoolean(KEY_VIBRATION, on).apply(); }
}