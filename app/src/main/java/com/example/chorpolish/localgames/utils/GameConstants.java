package com.rakibul.chorpolish.localgames.utils;

import android.graphics.Color;

public class GameConstants {
    public static final int MAX_NUTS_PER_TUBE = 4;
    public static final int INITIAL_TUBES = 5;
    
    // Colors for Nuts
    public static final int[] NUT_COLORS = {
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FFEB3B"), // Yellow
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#FF9800")  // Orange
    };
    
    // Animation Durations
    public static final int ANIM_DURATION_MOVE = 300;
    public static final int ANIM_DURATION_LIFT = 150;
    public static final int ANIM_DURATION_SHAKE = 400;
}