package com.rakibul.chorpolish.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakibul.chorpolish.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    val adManager: com.rakibul.chorpolish.utils.AdManager
) : ViewModel() {

    val soundEffectsEnabled: StateFlow<Boolean> = sessionManager.soundEffectsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val backgroundMusicEnabled: StateFlow<Boolean> = sessionManager.backgroundMusicEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val characterVoiceEnabled: StateFlow<Boolean> = sessionManager.characterVoiceEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkThemeEnabled: StateFlow<Boolean?> = sessionManager.darkThemeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val language: StateFlow<String> = sessionManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val premiumThemesUnlocked: StateFlow<Boolean> = sessionManager.premiumThemesUnlocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val premiumEmojisUnlocked: StateFlow<Boolean> = sessionManager.premiumEmojisUnlocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val longMatchUnlocked: StateFlow<Boolean> = sessionManager.longMatchUnlocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setSoundEffectsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setSoundEffectsEnabled(enabled)
        }
    }

    fun setBackgroundMusicEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setBackgroundMusicEnabled(enabled)
        }
    }

    fun setCharacterVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setCharacterVoiceEnabled(enabled)
        }
    }

    fun setDarkThemeEnabled(enabled: Boolean?) {
        viewModelScope.launch {
            sessionManager.setDarkThemeEnabled(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            sessionManager.setLanguage(language)
        }
    }

    fun unlockPremiumThemes() {
        viewModelScope.launch {
            sessionManager.setPremiumThemesUnlocked(true)
        }
    }

    fun unlockPremiumEmojis() {
        viewModelScope.launch {
            sessionManager.setPremiumEmojisUnlocked(true)
        }
    }

    fun unlockLongMatch() {
        viewModelScope.launch {
            sessionManager.setLongMatchUnlocked(true)
        }
    }
}
