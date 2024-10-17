package com.example.playlistmaker.data.impl

import android.app.Application.MODE_PRIVATE
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingsDataStore

class SettingsDataStoreImpl(private val context: Context): SettingsDataStore {

    private val sharedPrefs by lazy {
        context.getSharedPreferences(KEY_APP_IS_DARK_THEME, MODE_PRIVATE)
    }

    override fun switchTheme(value: Boolean?, rememberState: Boolean){
        val currentValue = value ?: isDarkTheme()
        AppCompatDelegate.setDefaultNightMode(
            if (currentValue) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        if (value != null) {
            sharedPrefs.edit()
                .putBoolean(KEY_APP_IS_DARK_THEME, currentValue)
                .apply()
        }
    }

    override fun getCurrentIsDarkTheme(): Boolean = isDarkTheme()

    private fun isDarkTheme(): Boolean{
        if (!sharedPrefs.contains(KEY_APP_IS_DARK_THEME)) {
            val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> return false
                Configuration.UI_MODE_NIGHT_YES -> return true
            }
            return false
        }
        else return sharedPrefs.getBoolean(KEY_APP_IS_DARK_THEME, false)
    }

    companion object{
        const val KEY_APP_IS_DARK_THEME = "IS_DARK_THEME"
    }
}