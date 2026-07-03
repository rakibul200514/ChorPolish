package com.example.chorpolish.utils

import com.example.chorpolish.BuildConfig

object NetworkConfig {
    // Replace with your actual production URL
    private const val PRODUCTION_URL = "https://chor-polish-backend.onrender.com"
    private const val DEVELOPMENT_URL = "http://10.32.112.144:3000" // Standard Android Emulator Localhost

    val BASE_URL: String
        get() = if (BuildConfig.DEBUG) "$DEVELOPMENT_URL/api/" else "$PRODUCTION_URL/api/"

    val SOCKET_URL: String
        get() = if (BuildConfig.DEBUG) DEVELOPMENT_URL else PRODUCTION_URL
}
