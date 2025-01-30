package com.example.playlistmaker.ui.media_library.state

import com.example.playlistmaker.domain.model.Track

sealed class FavouriteTracksScreenState {
    data object Loading: FavouriteTracksScreenState()
    data class Empty(val message: String): FavouriteTracksScreenState()
    data class Content(val trackList: List<Track>): FavouriteTracksScreenState()
}