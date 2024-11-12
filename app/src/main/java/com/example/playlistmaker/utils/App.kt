package com.example.playlistmaker.utils

import android.app.Application
import com.example.playlistmaker.creator.Creator

class App : Application() {
    val settingsDataStoreInteractor by lazy {
        Creator.provideSettingsDataStore(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        settingsDataStoreInteractor.switchTheme(null)
    }
}