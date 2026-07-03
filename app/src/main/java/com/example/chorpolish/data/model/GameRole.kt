package com.example.chorpolish.data.model

import com.example.chorpolish.R

enum class GameRole(val roleName: String, val baseScore: Int, val imageRes: Int) {
    RAJA("King", 1000, R.drawable.role_raja),
    MANTRI("Minister", 800, R.drawable.role_mantri),
    SIPAHI("Police", 500, R.drawable.role_sipahi),
    CHOR("Thief", 0, R.drawable.role_chor);

    companion object {
        fun getAllRoles(): List<GameRole> = values().toList()
    }
}
