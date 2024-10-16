package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.impl.SettingsDataStoreImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.TracksSearchHistoryManager
import com.example.playlistmaker.domain.api.SettingsDataStoreUseCaseApi
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SettingsDataStoreUseCase
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient(), TracksSearchHistoryManager(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideSettingsDataStore(context: Context): SettingsDataStoreUseCaseApi{
        return SettingsDataStoreUseCase(SettingsDataStoreImpl(context))
    }
}