package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class StatisticsManager {
    private static final String PREF_NAME = "GameStatsPrefs";
    private static final String KEY_TOTAL_MOVES = "total_moves";
    private static final String KEY_TOTAL_HINTS = "total_hints";
    private static final String KEY_TOTAL_UNDO = "total_undo";
    private static final String KEY_TOTAL_COINS_EARNED = "total_coins_earned";
    private static final String KEY_TOTAL_PLAY_TIME = "total_play_time"; // in seconds

    private final SharedPreferences prefs;

    public StatisticsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addMove() { prefs.edit().putInt(KEY_TOTAL_MOVES, getTotalMoves() + 1).apply(); }
    public int getTotalMoves() { return prefs.getInt(KEY_TOTAL_MOVES, 0); }

    public void addHint() { prefs.edit().putInt(KEY_TOTAL_HINTS, getTotalHints() + 1).apply(); }
    public int getTotalHints() { return prefs.getInt(KEY_TOTAL_HINTS, 0); }

    public void addUndo() { prefs.edit().putInt(KEY_TOTAL_UNDO, getTotalUndo() + 1).apply(); }
    public int getTotalUndo() { return prefs.getInt(KEY_TOTAL_UNDO, 0); }

    public void addCoinsEarned(int amount) { prefs.edit().putInt(KEY_TOTAL_COINS_EARNED, getTotalCoinsEarned() + amount).apply(); }
    public int getTotalCoinsEarned() { return prefs.getInt(KEY_TOTAL_COINS_EARNED, 0); }

    public void addPlayTime(long seconds) { prefs.edit().putLong(KEY_TOTAL_PLAY_TIME, getTotalPlayTime() + seconds).apply(); }
    public long getTotalPlayTime() { return prefs.getLong(KEY_TOTAL_PLAY_TIME, 0); }
}