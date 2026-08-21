package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GemManager {
    private static final String PREF_NAME = "GemsPrefs";
    private static final String KEY_GEMS = "total_gems";
    private static final String KEY_TROPHIES = "total_trophies";
    private final SharedPreferences prefs;

    public GemManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getGems() {
        return prefs.getInt(KEY_GEMS, 0);
    }

    public void addGems(int amount) {
        prefs.edit().putInt(KEY_GEMS, getGems() + amount).apply();
    }

    public int getTrophies() {
        return prefs.getInt(KEY_TROPHIES, 0);
    }

    public void addTrophy() {
        prefs.edit().putInt(KEY_TROPHIES, getTrophies() + 1).apply();
    }
}