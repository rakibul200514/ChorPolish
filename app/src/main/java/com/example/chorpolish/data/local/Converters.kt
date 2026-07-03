package com.example.chorpolish.data.local

import androidx.room.TypeConverter
import com.example.chorpolish.data.model.GameRole

class Converters {
    @TypeConverter
    fun fromGameRole(role: GameRole): String = role.name

    @TypeConverter
    fun toGameRole(value: String): GameRole = GameRole.valueOf(value)
}
