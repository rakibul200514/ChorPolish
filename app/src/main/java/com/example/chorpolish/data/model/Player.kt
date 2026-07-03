package com.example.chorpolish.data.model

data class Player(
    val id: Int,
    val name: String,
    var currentRole: GameRole? = null,
    var sessionScore: Int = 0,
    var hasSeenRole: Boolean = false
)
