package com.rakibul.chorpolish.localgames.utils;

import android.graphics.Color;

public class GameConstants {
    public static final int MAX_NUTS_PER_TUBE = 4;
    public static final int INITIAL_TUBES = 5;
    
    public static final int[] NUT_COLORS = {
            Color.parseColor("#F44336"), // Red
            Color.parseColor("#4CAF50"), // Green
            Color.parseColor("#FFEB3B"), // Yellow
            Color.parseColor("#2196F3"), // Blue
            Color.parseColor("#9C27B0"), // Purple
            Color.parseColor("#FF9800"), // Orange
            Color.parseColor("#00BCD4"), // Cyan
            Color.parseColor("#E91E63")  // Pink
    };
    
    public static final int ANIM_DURATION_MOVE = 350;
    public static final int ANIM_DURATION_SHAKE = 400;
}