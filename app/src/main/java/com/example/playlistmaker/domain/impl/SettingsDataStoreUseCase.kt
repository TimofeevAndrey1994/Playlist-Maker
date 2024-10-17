package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsDataStore
import com.example.playlistmaker.domain.api.SettingsDataStoreUseCaseApi

class SettingsDataStoreUseCase(private val settingsDataStore: SettingsDataStore): SettingsDataStoreUseCaseApi {

    override fun switchTheme(value: Boolean?) {
        settingsDataStore.switchTheme(value)
    }

    override fun getCurrentDarkThemeValue(): Boolean{
        return settingsDataStore.getCurrentIsDarkTheme()
    }

}