package com.example.playlistmaker.utils

import android.app.Application
import com.example.playlistmaker.data.di.dataModule
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.di.domainModule
import com.example.playlistmaker.ui.di.viewModelsModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    private lateinit var settingsDataStoreInteractor: SettingsDataStoreInteractor

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@App)
            modules(dataModule, domainModule, viewModelsModule)
        }
        settingsDataStoreInteractor = getKoin().get()
        settingsDataStoreInteractor.switchTheme(null)
    }
}