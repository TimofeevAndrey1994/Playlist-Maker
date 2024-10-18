package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.impl.SettingsDataRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.impl.TracksSearchHistoryManager
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractorApi
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient(), TracksSearchHistoryManager(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideSettingsDataStore(context: Context): SettingsDataStoreInteractorApi{
        return SettingsDataStoreInteractor(SettingsDataRepositoryImpl(context))
    }
}