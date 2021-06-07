/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.content.SharedPreferences
import java.util.*

class AppPreferences(private val preferences: SharedPreferences) {
    val serviceUrl: String
        get() {
            val url = preferences.getString("service_url", null)
            return if(url.isNullOrBlank()) {
                "ws://192.168.0.80:9000"
            } else {
                url
            }
        }

    val locale: Locale?
        get() = when(val l = preferences.getString("locale", "default")) {
            "default", null -> null
            else -> Locale(l)
        }

    val speechRate: Float
        get() = when(preferences.getString("speech_rate", "")) {
            "very_fast" -> 2.00f
            "fast" -> 1.50f
            "slow" -> 0.75f
            "very_slow" -> 0.50f
            else -> 1.00f
        }
}
