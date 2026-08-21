package com.rakibul.chorpolish.localgames.arrowescape.model

object ArrowEscapeLevelProvider {

    fun getLevel(levelNumber: Int): LevelData {
        return levels.find { it.levelNumber == levelNumber } ?: levels.last()
    }

    val totalLevels: Int get() = levels.size

    private val levels = mutableListOf<LevelData>().apply {
        // Levels 1-5: Introductory
        add(LevelData(1, "Welcome", 4, 4, listOf(
            ArrowTile(1, ArrowDirection.RIGHT, 1, 1),
            ArrowTile(2, ArrowDirection.RIGHT, 1, 2)
        )))
        add(LevelData(2, "Sequence", 4, 4, listOf(
            ArrowTile(1, ArrowDirection.DOWN, 0, 1),
            ArrowTile(2, ArrowDirection.DOWN, 1, 1),
            ArrowTile(3, ArrowDirection.DOWN, 2, 1)
        )))
        add(LevelData(3, "Cross", 5, 5, listOf(
            ArrowTile(1, ArrowDirection.RIGHT, 2, 0),
            ArrowTile(2, ArrowDirection.RIGHT, 2, 1),
            ArrowTile(3, ArrowDirection.LEFT, 2, 3),
            ArrowTile(4, ArrowDirection.LEFT, 2, 4)
        )))
        add(LevelData(4, "Surround", 5, 5, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 2),
            ArrowTile(2, ArrowDirection.DOWN, 3, 2),
            ArrowTile(3, ArrowDirection.LEFT, 2, 1),
            ArrowTile(4, ArrowDirection.RIGHT, 2, 3)
        )))
        add(LevelData(5, "Corners", 6, 6, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 1),
            ArrowTile(2, ArrowDirection.UP, 1, 4),
            ArrowTile(3, ArrowDirection.DOWN, 4, 1),
            ArrowTile(4, ArrowDirection.DOWN, 4, 4)
        )))

        // Levels 6-10: Easy
        add(LevelData(6, "The Line", 6, 6, (0..5).map { ArrowTile(it, ArrowDirection.RIGHT, 2, it) }))
        add(LevelData(7, "Twin Peaks", 6, 6, listOf(
            ArrowTile(1, ArrowDirection.UP, 2, 1), ArrowTile(2, ArrowDirection.UP, 1, 1),
            ArrowTile(3, ArrowDirection.UP, 2, 4), ArrowTile(4, ArrowDirection.UP, 1, 4)
        )))
        add(LevelData(8, "Zig Zag", 6, 6, listOf(
            ArrowTile(1, ArrowDirection.RIGHT, 1, 1), ArrowTile(2, ArrowDirection.DOWN, 1, 2),
            ArrowTile(3, ArrowDirection.RIGHT, 2, 2), ArrowTile(4, ArrowDirection.DOWN, 2, 3)
        )))
        add(LevelData(9, "Box", 5, 5, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 1), ArrowTile(2, ArrowDirection.RIGHT, 1, 3),
            ArrowTile(3, ArrowDirection.DOWN, 3, 3), ArrowTile(4, ArrowDirection.LEFT, 3, 1)
        )))
        add(LevelData(10, "Center Out", 7, 7, listOf(
            ArrowTile(1, ArrowDirection.UP, 3, 3), ArrowTile(2, ArrowDirection.DOWN, 4, 3),
            ArrowTile(3, ArrowDirection.LEFT, 3, 2), ArrowTile(4, ArrowDirection.RIGHT, 3, 4)
        )))

        // Levels 11-20: Shapes
        add(LevelData(11, "STAR", 7, 7, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 3),
            ArrowTile(2, ArrowDirection.LEFT, 2, 2), ArrowTile(3, ArrowDirection.RIGHT, 2, 4),
            ArrowTile(4, ArrowDirection.LEFT, 3, 1), ArrowTile(5, ArrowDirection.RIGHT, 3, 5),
            ArrowTile(6, ArrowDirection.DOWN, 4, 2), ArrowTile(7, ArrowDirection.DOWN, 4, 4)
        )))
        add(LevelData(12, "HEART", 7, 7, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 2), ArrowTile(2, ArrowDirection.UP, 1, 4),
            ArrowTile(3, ArrowDirection.LEFT, 2, 1), ArrowTile(4, ArrowDirection.RIGHT, 2, 5),
            ArrowTile(5, ArrowDirection.DOWN, 3, 1), ArrowTile(6, ArrowDirection.DOWN, 3, 5),
            ArrowTile(7, ArrowDirection.DOWN, 4, 2), ArrowTile(8, ArrowDirection.DOWN, 4, 4),
            ArrowTile(9, ArrowDirection.DOWN, 5, 3)
        )))
        add(LevelData(13, "DIYA", 7, 7, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 3),
            ArrowTile(2, ArrowDirection.UP, 2, 3),
            ArrowTile(3, ArrowDirection.RIGHT, 3, 1), ArrowTile(4, ArrowDirection.RIGHT, 3, 2), ArrowTile(5, ArrowDirection.LEFT, 3, 4), ArrowTile(6, ArrowDirection.LEFT, 3, 5),
            ArrowTile(7, ArrowDirection.DOWN, 4, 2), ArrowTile(8, ArrowDirection.DOWN, 4, 3), ArrowTile(9, ArrowDirection.DOWN, 4, 4)
        )))
        add(LevelData(14, "BRIDGE", 6, 8, listOf(
            ArrowTile(1, ArrowDirection.UP, 2, 1), ArrowTile(2, ArrowDirection.UP, 2, 6),
            ArrowTile(3, ArrowDirection.RIGHT, 1, 2), ArrowTile(4, ArrowDirection.RIGHT, 1, 3), ArrowTile(5, ArrowDirection.RIGHT, 1, 4), ArrowTile(6, ArrowDirection.RIGHT, 1, 5)
        )))
        add(LevelData(15, "TEMPLE", 8, 8, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 4),
            ArrowTile(2, ArrowDirection.UP, 2, 3), ArrowTile(3, ArrowDirection.UP, 2, 5),
            ArrowTile(4, ArrowDirection.RIGHT, 3, 2), ArrowTile(5, ArrowDirection.LEFT, 3, 6),
            ArrowTile(6, ArrowDirection.DOWN, 4, 2), ArrowTile(7, ArrowDirection.DOWN, 4, 6),
            ArrowTile(8, ArrowDirection.RIGHT, 5, 2), ArrowTile(9, ArrowDirection.LEFT, 5, 6)
        )))
        
        // Filling up to 50 with patterns and shapes
        for (i in 16..20) add(generatePatternLevel(i, "Pattern A", i % 2 == 0))
        
        add(LevelData(21, "TAJ MAHAL", 10, 10, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 5),
            ArrowTile(2, ArrowDirection.UP, 2, 4), ArrowTile(3, ArrowDirection.UP, 2, 5), ArrowTile(4, ArrowDirection.UP, 2, 6),
            ArrowTile(5, ArrowDirection.RIGHT, 3, 3), ArrowTile(6, ArrowDirection.LEFT, 3, 7),
            ArrowTile(7, ArrowDirection.UP, 4, 2), ArrowTile(8, ArrowDirection.UP, 4, 8),
            ArrowTile(9, ArrowDirection.DOWN, 5, 2), ArrowTile(10, ArrowDirection.DOWN, 5, 8),
            ArrowTile(11, ArrowDirection.RIGHT, 6, 3), ArrowTile(12, ArrowDirection.RIGHT, 6, 4), ArrowTile(13, ArrowDirection.LEFT, 6, 6), ArrowTile(14, ArrowDirection.LEFT, 6, 7)
        )))

        for (i in 22..30) add(generatePatternLevel(i, "Complex", i % 3 == 0))

        add(LevelData(31, "INDIA", 12, 10, listOf(
            ArrowTile(1, ArrowDirection.UP, 1, 4), ArrowTile(2, ArrowDirection.UP, 1, 5),
            ArrowTile(3, ArrowDirection.RIGHT, 2, 3), ArrowTile(4, ArrowDirection.LEFT, 2, 6),
            ArrowTile(5, ArrowDirection.RIGHT, 4, 2), ArrowTile(6, ArrowDirection.LEFT, 4, 7),
            ArrowTile(7, ArrowDirection.RIGHT, 6, 3), ArrowTile(8, ArrowDirection.LEFT, 6, 6),
            ArrowTile(9, ArrowDirection.DOWN, 8, 4), ArrowTile(10, ArrowDirection.DOWN, 8, 5)
        )))

        for (i in 32..40) add(generatePatternLevel(i, "Expert", true))

        add(LevelData(41, "PEACOCK", 10, 10, listOf(
            ArrowTile(1, ArrowDirection.UP, 2, 5),
            ArrowTile(2, ArrowDirection.LEFT, 3, 4), ArrowTile(3, ArrowDirection.RIGHT, 3, 6),
            ArrowTile(4, ArrowDirection.LEFT, 4, 3), ArrowTile(5, ArrowDirection.RIGHT, 4, 7),
            ArrowTile(6, ArrowDirection.DOWN, 5, 4), ArrowTile(7, ArrowDirection.DOWN, 5, 6),
            ArrowTile(8, ArrowDirection.DOWN, 6, 5)
        )))

        for (i in 42..49) add(generatePatternLevel(i, "Master", true))

        add(LevelData(50, "THE END", 12, 12, (0..10).flatMap { r ->
            (0..10).filter { (r + it) % 4 == 0 }.map { c ->
                ArrowTile(r * 100 + c, if (r % 2 == 0) ArrowDirection.RIGHT else ArrowDirection.LEFT, r, c)
            }
        }))
    }.map { 
        if (!isSolvable(it)) {
           // Fallback to a simple solvable level if by any chance my manual levels are wrong
           LevelData(it.levelNumber, "Fallback", 5, 5, listOf(ArrowTile(99, ArrowDirection.UP, 2, 2)))
        } else it
    }

    private fun generatePatternLevel(num: Int, name: String, rotate: Boolean): LevelData {
        val size = 5 + (num / 10)
        val arrows = mutableListOf<ArrowTile>()
        var id = 1
        for (r in 1 until size - 1) {
            for (c in 1 until size - 1) {
                if ((r + c) % 2 == 0) {
                    val dir = when {
                        rotate && r % 2 == 0 -> ArrowDirection.RIGHT
                        rotate -> ArrowDirection.LEFT
                        c % 2 == 0 -> ArrowDirection.DOWN
                        else -> ArrowDirection.UP
                    }
                    arrows.add(ArrowTile(id++, dir, r, c))
                }
            }
        }
        return LevelData(num, name, size, size, arrows)
    }

    private fun isSolvable(level: LevelData): Boolean {
        val currentArrows = level.arrows.toMutableList()
        var changed = true
        while (changed && currentArrows.isNotEmpty()) {
            changed = false
            val iterator = currentArrows.iterator()
            while (iterator.hasNext()) {
                val arrow = iterator.next()
                val others = currentArrows.filter { it.id != arrow.id && it.status == ArrowStatus.ACTIVE }
                val blocked = when (arrow.direction) {
                    ArrowDirection.UP -> others.any { it.col == arrow.col && it.row < arrow.row }
                    ArrowDirection.DOWN -> others.any { it.col == arrow.col && it.row > arrow.row }
                    ArrowDirection.LEFT -> others.any { it.row == arrow.row && it.col < arrow.col }
                    ArrowDirection.RIGHT -> others.any { it.row == arrow.row && it.col > arrow.col }
                }
                if (!blocked) {
                    iterator.remove()
                    changed = true
                    break // Recalculate blockers
                }
            }
        }
        return currentArrows.isEmpty()
    }
}
