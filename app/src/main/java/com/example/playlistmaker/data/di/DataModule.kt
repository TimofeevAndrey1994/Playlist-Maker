package com.example.playlistmaker.data.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.convertors.PlaylistConvertor
import com.example.playlistmaker.data.convertors.TrackConvertor
import com.example.playlistmaker.data.db.AppDataBase
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
import com.example.playlistmaker.data.impl.PlaylistRepositoryImpl
import com.example.playlistmaker.data.internal_storage.InternalStorageManager
import com.example.playlistmaker.data.internal_storage.InternalStorageManagerImpl
import com.example.playlistmaker.data.track_local_storage.api.TracksLocalStorage
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorageManager.Companion.SEARCH_HISTORY_KEY
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.api.SettingsDataRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<TracksRepository> {
        TrackRepositoryImpl(get(), get(), get(), get(), get())
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get(), get(), get())
    }
    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }
    single<TracksLocalStorage> {
        val preferences =
            androidContext().getSharedPreferences(SEARCH_HISTORY_KEY, Context.MODE_PRIVATE)
        TracksLocalStorageManager(get(), preferences)
    }
    single<SettingsDataRepository> {
        val preferences =
            androidContext().getSharedPreferences(KEY_APP_IS_DARK_THEME, Context.MODE_PRIVATE)
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
        MediaPlayer()
    }
    factory {
        TrackConvertor()
    }
    factory {
        PlaylistConvertor()
    }
    factory<InternalStorageManager> {
        InternalStorageManagerImpl(get())
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

val dataBaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDataBase::class.java, "dataBase.db").build()
    }
}