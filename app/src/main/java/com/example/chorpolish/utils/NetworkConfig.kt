package com.rakibul.chorpolish.utils

import com.rakibul.chorpolish.BuildConfig

object NetworkConfig {
    // Replace with your actual production URL
    private const val PRODUCTION_URL = "https://chorpolish.onrender.com"
    private const val DEVELOPMENT_URL = "https://chorpolish.onrender.com" // Set to production for testing

    val BASE_URL: String
        get() = if (BuildConfig.DEBUG) "$DEVELOPMENT_URL/api/" else "$PRODUCTION_URL/api/"

    val SOCKET_URL: String
        get() = if (BuildConfig.DEBUG) DEVELOPMENT_URL else PRODUCTION_URL
}
