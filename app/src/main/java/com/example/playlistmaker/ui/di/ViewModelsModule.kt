package com.example.playlistmaker.ui.di

import com.example.playlistmaker.ui.media_library.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.ui.media_library.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.ui.playlist_details.view_model.DetailsPlaylistViewModel
import com.example.playlistmaker.ui.playlist_details.view_model.EditPlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { (trackId: Long) ->
        MediaPlayerViewModel(trackId, get(), get(), get())
    }
    viewModel { (playlistId: Int) ->
        DetailsPlaylistViewModel(playlistId, get())
    }
    viewModel {
        SearchViewModel(get())
    }
    viewModel{
        SettingsViewModel(get(), get())
    }
    viewModel{
        FavouriteTracksViewModel(get())
    }
    viewModel {
        PlaylistsViewModel(get())
    }
    viewModel {(playlistId: Int) ->
        EditPlaylistViewModel(playlistId, get())
    }
}