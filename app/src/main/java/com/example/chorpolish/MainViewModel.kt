package com.example.chorpolish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorpolish.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    val darkThemeEnabled: StateFlow<Boolean?> = sessionManager.darkThemeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val language: StateFlow<String> = sessionManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val premiumThemesUnlocked: StateFlow<Boolean> = sessionManager.premiumThemesUnlocked
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}
