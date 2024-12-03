package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsDataStoreInteractor: SettingsDataStoreInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val currentDarkThemeState = MutableLiveData<Boolean>()
    fun observeCurrentDarkTheme(): LiveData<Boolean> = currentDarkThemeState

    init {
        currentDarkThemeState.value = settingsDataStoreInteractor.getCurrentDarkThemeValue()
    }

    fun switchTheme(value: Boolean) {
        settingsDataStoreInteractor.switchTheme(value)
        currentDarkThemeState.postValue(value)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun textToSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}