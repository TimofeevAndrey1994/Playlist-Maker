package com.example.playlistmaker.ui.di

import com.example.playlistmaker.ui.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { (trackId: Long) ->
        MediaPlayerViewModel(trackId)
    }
    viewModel {
        SearchViewModel()
    }
    viewModel{
        SettingsViewModel()
    }
}