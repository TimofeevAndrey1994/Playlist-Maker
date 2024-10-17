package com.example.playlistmaker.domain.api

interface SettingsDataStore {
    fun switchTheme(value: Boolean?, rememberState: Boolean = false)
    fun getCurrentIsDarkTheme(): Boolean
}