package com.example.playlistmaker.domain.api

interface SettingsDataStoreInteractorApi {
    fun switchTheme(value: Boolean?)
    fun getCurrentDarkThemeValue(): Boolean
}