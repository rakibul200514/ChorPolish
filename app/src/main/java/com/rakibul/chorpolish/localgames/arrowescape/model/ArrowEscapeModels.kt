package com.rakibul.chorpolish.localgames.arrowescape.model

enum class ArrowDirection {
    UP, DOWN, LEFT, RIGHT
}

data class ArrowTile(
    val id: Int,
    val direction: ArrowDirection,
    val row: Int,
    val col: Int,
    val isStatic: Boolean = false
)

data class LevelData(
    val levelNumber: Int,
    val levelName: String = "",
    val rows: Int,
    val cols: Int,
    val arrows: List<ArrowTile>
)
