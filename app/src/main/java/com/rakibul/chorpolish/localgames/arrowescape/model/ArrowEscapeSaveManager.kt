package com.rakibul.chorpolish.localgames.arrowescape.model

import android.content.Context
import android.content.SharedPreferences

class ArrowEscapeSaveManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("ArrowEscapeSave", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_UNLOCKED_LEVEL = "unlocked_level"
    }

    fun getUnlockedLevel(): Int = prefs.getInt(KEY_UNLOCKED_LEVEL, 1)

    fun unlockLevel(level: Int) {
        if (level > getUnlockedLevel()) {
            prefs.edit().putInt(KEY_UNLOCKED_LEVEL, level).apply()
        }
    }
}
