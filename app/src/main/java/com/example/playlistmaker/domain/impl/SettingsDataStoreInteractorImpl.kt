package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsDataRepository
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor

class SettingsDataStoreInteractorImpl(private val settingsDataRepository: SettingsDataRepository):
    SettingsDataStoreInteractor {

    override fun switchTheme(value: Boolean?) {
        settingsDataRepository.switchTheme(value)
    }

    override fun getCurrentDarkThemeValue(): Boolean{
        return settingsDataRepository.getCurrentIsDarkTheme()
    }

}