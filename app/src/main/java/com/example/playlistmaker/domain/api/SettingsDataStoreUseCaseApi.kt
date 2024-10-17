package com.example.playlistmaker.domain.api

interface SettingsDataStoreUseCaseApi {
    fun switchTheme(value: Boolean?)
    fun getCurrentDarkThemeValue(): Boolean
}