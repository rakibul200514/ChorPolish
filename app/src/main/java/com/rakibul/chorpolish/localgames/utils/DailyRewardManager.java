package com.rakibul.chorpolish.localgames.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.concurrent.TimeUnit;

public class DailyRewardManager {
    private static final String PREF_NAME = "DailyRewardPrefs";
    private static final String KEY_LAST_CLAIM_TIME = "last_claim_time";
    private static final String KEY_CONSECUTIVE_DAYS = "consecutive_days";

    private final SharedPreferences prefs;
    private final int[] REWARDS = {100, 150, 200, 250, 300, 400, 500};

    public DailyRewardManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean canClaimReward() {
        long lastClaim = prefs.getLong(KEY_LAST_CLAIM_TIME, 0);
        long currentTime = System.currentTimeMillis();
        
        if (lastClaim == 0) return true;

        long diffInMillis = currentTime - lastClaim;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

        return diffInDays >= 1;
    }

    public int getNextRewardAmount() {
        int day = getConsecutiveDays();
        return REWARDS[day % 7];
    }

    public int getConsecutiveDays() {
        long lastClaim = prefs.getLong(KEY_LAST_CLAIM_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long diffInDays = TimeUnit.MILLISECONDS.toDays(currentTime - lastClaim);

        if (diffInDays > 1) {
            // Missed a day, reset but let's keep it simple for now and just return the current count or reset
            prefs.edit().putInt(KEY_CONSECUTIVE_DAYS, 0).apply();
            return 0;
        }
        return prefs.getInt(KEY_CONSECUTIVE_DAYS, 0);
    }

    public void claimReward() {
        int day = getConsecutiveDays();
        prefs.edit()
                .putLong(KEY_LAST_CLAIM_TIME, System.currentTimeMillis())
                .putInt(KEY_CONSECUTIVE_DAYS, (day + 1) % 7)
                .apply();
    }
}