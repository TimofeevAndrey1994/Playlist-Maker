package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    private val sharedPrefs by lazy {
        getSharedPreferences(KEY_APP_IS_DARK_THEME, MODE_PRIVATE)
    }

    var darkTheme: Boolean
        get() {
            if (!sharedPrefs.contains(KEY_APP_IS_DARK_THEME)) {
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                when (currentNightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> return false
                    Configuration.UI_MODE_NIGHT_YES -> return true
                }
                return false
            }
            else return sharedPrefs.getBoolean(KEY_APP_IS_DARK_THEME, false)
        }
        set(value) {
            sharedPrefs.edit()
                .putBoolean(KEY_APP_IS_DARK_THEME, value)
                .apply()
        }

    override fun onCreate() {
        super.onCreate()

        switchTheme(darkTheme)
    }

    fun switchTheme(checkToDarkTheme: Boolean, rememberState: Boolean = false) {
        AppCompatDelegate.setDefaultNightMode(
            if (checkToDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        if (rememberState) darkTheme = checkToDarkTheme
    }

    companion object{
        const val KEY_APP_IS_DARK_THEME = "IS_DARK_THEME"
    }
}