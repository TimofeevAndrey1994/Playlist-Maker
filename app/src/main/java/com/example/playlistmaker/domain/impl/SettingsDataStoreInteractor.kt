package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsDataRepository
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractorApi

class SettingsDataStoreInteractor(private val settingsDataRepository: SettingsDataRepository): SettingsDataStoreInteractorApi {

    override fun switchTheme(value: Boolean?) {
        settingsDataRepository.switchTheme(value)
    }

    override fun getCurrentDarkThemeValue(): Boolean{
        return settingsDataRepository.getCurrentIsDarkTheme()
    }

}