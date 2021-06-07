/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class AppActivity : AppCompatActivity() {
    companion object {
        var appLocale: Locale? = null
    }

    init {
        appLocale?.let { locale ->
            Locale.setDefault(locale)
            Configuration().also { config ->
                config.setLocale(locale)
                applyOverrideConfiguration(config)
            }
        }
    }

    fun updateAppConfig() {
        appLocale?.let { locale ->
            Locale.setDefault(locale)
            resources.configuration.also { config ->
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
    }
}