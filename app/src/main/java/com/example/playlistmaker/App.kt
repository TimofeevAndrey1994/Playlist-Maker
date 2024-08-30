package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App: Application() {

    private val sharedPrefs by lazy {
        getSharedPreferences(KEY_APP_IS_DARK_THEME, MODE_PRIVATE)
    }

     var darkTheme: Boolean
        get() = sharedPrefs.getBoolean(KEY_APP_IS_DARK_THEME, false)
        set(value) {
            sharedPrefs.edit()
                .putBoolean(KEY_APP_IS_DARK_THEME, value)
                .apply()
        }

    override fun onCreate() {
        super.onCreate()

        switchTheme(darkTheme)
    }

    fun switchTheme(checkToDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (checkToDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(KEY_APP_IS_DARK_THEME, checkToDarkTheme)
            .apply()
    }

    companion object{
        const val KEY_APP_IS_DARK_THEME = "IS_DARK_THEME"
    }
}