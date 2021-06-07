/*
 * EYE5G SYSTEM
 * Copyright (c) 2021 SilentByte <https://silentbyte.com/>
 */

package com.silentbyte.eye5g

import android.app.Application
import androidx.preference.PreferenceManager

@Suppress("unused")
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppPreferences(PreferenceManager.getDefaultSharedPreferences(this)).also {
            AppActivity.appLocale = it.locale
        }
    }
}
