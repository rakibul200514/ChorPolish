package com.rakibul.chorpolish.localgames.arrowescape.viewmodel

import androidx.lifecycle.ViewModel
import com.rakibul.chorpolish.localgames.arrowescape.model.ArrowDirection
import com.rakibul.chorpolish.localgames.arrowescape.model.ArrowTile
import com.rakibul.chorescape.localgames.arrowescape.model.LevelData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// Placeholder Level Data for Part 1
private val Level1 = com.rakibul.chorpolish.localgames.arrowescape.model.LevelData(
    levelNumber = 1,
    levelName = "START",
    rows = 4,
    cols = 4,
    arrows = listOf(
        ArrowTile(1, ArrowDirection.RIGHT, 1, 1),
        ArrowTile(2, ArrowDirection.DOWN, 1, 2),
        ArrowTile(3, ArrowDirection.LEFT, 2, 2),
        ArrowTile(4, ArrowDirection.UP, 2, 1)
    )
)

@HiltViewModel
class ArrowEscapeViewModel @Inject constructor() : ViewModel() {

    private val _currentLevel = MutableStateFlow(Level1)
    val currentLevel: StateFlow<com.rakibul.chorpolish.localgames.arrowescape.model.LevelData> = _currentLevel.asStateFlow()

    private val _motivationalMessage = MutableStateFlow<String?>(null)
    val motivationalMessage: StateFlow<String?> = _motivationalMessage.asStateFlow()

    fun onUndo() {
        // Placeholder for Part 1
    }

    fun onHint() {
        // Placeholder for Part 1
    }

    fun onRestart() {
        // Placeholder for Part 1
    }
}
