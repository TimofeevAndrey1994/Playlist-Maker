package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.impl.SettingsDataRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.SettingsDataStoreInteractorImpl
import com.example.playlistmaker.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): TracksRepository{
        return TrackRepositoryImpl(RetrofitNetworkClient(), TracksLocalStorageManager(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor{
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideSettingsDataStore(context: Context): SettingsDataStoreInteractor {
        return SettingsDataStoreInteractorImpl(SettingsDataRepositoryImpl(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor{
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }
}