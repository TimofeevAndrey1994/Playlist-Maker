package com.example.playlistmaker.domain.api

interface SettingsDataStoreInteractor {
    fun switchTheme(value: Boolean?)
    fun getCurrentDarkThemeValue(): Boolean
}