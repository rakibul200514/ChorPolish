package com.rakibul.chorpolish.data.local

import androidx.room.TypeConverter
import com.rakibul.chorpolish.data.model.GameRole

class Converters {
    @TypeConverter
    fun fromGameRole(role: GameRole): String = role.name

    @TypeConverter
    fun toGameRole(value: String): GameRole = GameRole.valueOf(value)
}
