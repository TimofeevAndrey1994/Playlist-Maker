package com.example.playlistmaker.domain.api

interface SettingsDataRepository {
    fun switchTheme(value: Boolean?)
    fun getCurrentIsDarkTheme(): Boolean
}