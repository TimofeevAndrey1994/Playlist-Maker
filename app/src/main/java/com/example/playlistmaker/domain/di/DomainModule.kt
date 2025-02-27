package com.example.playlistmaker.domain.di

import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.SettingsDataStoreInteractor
import com.example.playlistmaker.domain.api.SharingInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.domain.impl.SettingsDataStoreInteractorImpl
import com.example.playlistmaker.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val domainModule = module {
    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }
    single<SettingsDataStoreInteractor>{
        SettingsDataStoreInteractorImpl(get())
    }
    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    single<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }
    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get(), get())
    }
}