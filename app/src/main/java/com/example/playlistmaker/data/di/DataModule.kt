package com.example.playlistmaker.data.di

import com.example.playlistmaker.data.impl.SettingsDataRepositoryImpl
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorage
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager
import com.example.playlistmaker.domain.api.SettingsDataRepository
import com.example.playlistmaker.domain.api.TracksRepository
import org.koin.dsl.module

val dataModule = module {
    single<TracksRepository> {
        TrackRepositoryImpl(get(), get(), get())
    }
    single<NetworkClient>{
        RetrofitNetworkClient(get())
    }
    single<TracksLocalStorage> {
        TracksLocalStorageManager(get())
    }
    single<SettingsDataRepository> {
        SettingsDataRepositoryImpl(get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
}