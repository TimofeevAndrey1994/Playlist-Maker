package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.utils.App

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

    fun textToSupport(){
        sharingInteractor.openSupport()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val settingsDataStoreInteractor =
                    (this[APPLICATION_KEY] as App).settingsDataStoreInteractor
                val sharingInteractor =
                    Creator.provideSharingInteractor((this[APPLICATION_KEY] as App))

                SettingsViewModel(settingsDataStoreInteractor, sharingInteractor)
            }
        }
    }
}