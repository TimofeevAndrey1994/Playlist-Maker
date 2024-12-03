package com.example.playlistmaker.data.di

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.data.impl.SettingsDataRepositoryImpl
import com.example.playlistmaker.data.impl.SettingsDataRepositoryImpl.Companion.KEY_APP_IS_DARK_THEME
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.data.impl.MediaPlayerManagerImpl
import com.example.playlistmaker.data.network.ItunesAPI
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.ExternalNavigator
import com.example.playlistmaker.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.track_local_storage.api.TracksLocalStorage
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager.Companion.SEARCH_HISTORY_KEY
import com.example.playlistmaker.domain.api.SettingsDataRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<TracksRepository> {
        TrackRepositoryImpl(get(), get(), get())
    }
    single<NetworkClient>{
        RetrofitNetworkClient(get(), get())
    }
    single<TracksLocalStorage> {
        val preferences = androidContext().getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
        TracksLocalStorageManager(get(), preferences)
    }
    single<SettingsDataRepository> {
        val preferences = androidContext().getSharedPreferences(KEY_APP_IS_DARK_THEME, Context.MODE_PRIVATE)
        SettingsDataRepositoryImpl(get(), preferences)
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
    single<Gson> {
        Gson()
    }
    single<MediaPlayerManager> {
        MediaPlayerManagerImpl(get())
    }
    single {
        Handler(Looper.getMainLooper())
    }
    single {
        MediaPlayer()
    }
}

val networkModule = module {
    single<ItunesAPI> {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ItunesAPI::class.java)
    }
}