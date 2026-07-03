package com.example.chorpolish.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface NavDestination : NavKey {
    @Serializable data object MainMenu : NavDestination
    @Serializable data class GameModeSelection(val isMultiplayer: Boolean = false) : NavDestination
    @Serializable data class MatchTypeSelection(val isMultiplayer: Boolean, val gameMode: String) : NavDestination
    @Serializable data object PlayerSetup : NavDestination
    @Serializable data object LetterSetPlay : NavDestination
    @Serializable data object MultiplayerEntry : NavDestination
    @Serializable data object MultiplayerLobby : NavDestination
    @Serializable data object RoleReveal : NavDestination
    @Serializable data object Guessing : NavDestination
    @Serializable data object Result : NavDestination
    @Serializable data object History : NavDestination
    @Serializable data object Settings : NavDestination
    @Serializable data object GameRules : NavDestination
}
