package com.example.chorpolish.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PLAYER_ID_KEY = stringPreferencesKey("player_id")
    private val ROOM_CODE_KEY = stringPreferencesKey("room_code")
    private val PLAYER_NAME_KEY = stringPreferencesKey("player_name")
    private val RULES_SEEN_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("rules_seen")
    private val SOUND_EFFECTS_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("sound_effects")
    private val BACKGROUND_MUSIC_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("background_music")
    private val CHARACTER_VOICE_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("character_voice")
    private val DARK_THEME_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("dark_theme")
    private val LANGUAGE_KEY = stringPreferencesKey("language")
    private val PREMIUM_THEMES_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("premium_themes")
    private val PREMIUM_EMOJIS_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("premium_emojis")
    private val LONG_MATCH_UNLOCKED_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("long_match_unlocked")

    val playerId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PLAYER_ID_KEY] ?: ""
    }

    val rulesSeen: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[RULES_SEEN_KEY] ?: false
    }

    val soundEffectsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[SOUND_EFFECTS_KEY] ?: true
    }

    val backgroundMusicEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BACKGROUND_MUSIC_KEY] ?: true
    }

    val characterVoiceEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[CHARACTER_VOICE_KEY] ?: true
    }

    val darkThemeEnabled: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[DARK_THEME_KEY]
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "en"
    }

    val premiumThemesUnlocked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PREMIUM_THEMES_KEY] ?: false
    }

    val premiumEmojisUnlocked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[PREMIUM_EMOJIS_KEY] ?: false
    }

    val longMatchUnlocked: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[LONG_MATCH_UNLOCKED_KEY] ?: false
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SOUND_EFFECTS_KEY] = enabled
        }
    }

    suspend fun setBackgroundMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BACKGROUND_MUSIC_KEY] = enabled
        }
    }

    suspend fun setCharacterVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[CHARACTER_VOICE_KEY] = enabled
        }
    }

    suspend fun setDarkThemeEnabled(enabled: Boolean?) {
        context.dataStore.edit { prefs ->
            if (enabled == null) {
                prefs.remove(DARK_THEME_KEY)
            } else {
                prefs[DARK_THEME_KEY] = enabled
            }
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
    }

    suspend fun setPremiumThemesUnlocked(unlocked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PREMIUM_THEMES_KEY] = unlocked
        }
    }

    suspend fun setPremiumEmojisUnlocked(unlocked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PREMIUM_EMOJIS_KEY] = unlocked
        }
    }

    suspend fun setLongMatchUnlocked(unlocked: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[LONG_MATCH_UNLOCKED_KEY] = unlocked
        }
    }

    suspend fun setRulesSeen(seen: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[RULES_SEEN_KEY] = seen
        }
    }

    val roomCode: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ROOM_CODE_KEY]
    }

    val playerName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PLAYER_NAME_KEY] ?: "Anonymous"
    }

    suspend fun savePlayerId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[PLAYER_ID_KEY] = id
        }
    }

    suspend fun saveRoomCode(code: String?) {
        context.dataStore.edit { prefs ->
            if (code == null) {
                prefs.remove(ROOM_CODE_KEY)
            } else {
                prefs[ROOM_CODE_KEY] = code
            }
        }
    }

    suspend fun savePlayerName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[PLAYER_NAME_KEY] = name
        }
    }
}
