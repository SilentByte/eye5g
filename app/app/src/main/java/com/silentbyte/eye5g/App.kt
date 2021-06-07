/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.app.Application
import android.preference.PreferenceManager
import java.util.*

@Suppress("unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        PreferenceManager.getDefaultSharedPreferences(this).getString("locale", null)?.let {
            AppActivity.appLocale = Locale(it)
        }

        // TODO: Add app setting.
        // AppActivity.appLocale = Locale("zh")
    }
}
