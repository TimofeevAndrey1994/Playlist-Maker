package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    val settingsDataStoreUseCase by lazy {
        Creator.provideSettingsDataStore(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        settingsDataStoreUseCase.switchTheme(null)
    }
}