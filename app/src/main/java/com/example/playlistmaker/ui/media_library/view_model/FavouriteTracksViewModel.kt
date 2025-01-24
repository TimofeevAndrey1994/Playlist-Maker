package com.example.playlistmaker.ui.media_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.ui.media_library.state.FavouriteTracksScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavouriteTracksScreenState?>(null)
    val uiState: StateFlow<FavouriteTracksScreenState?> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = FavouriteTracksScreenState.Loading
            tracksInteractor.getFavouriteTracks().collect { pair ->
                val tracks = pair.first
                val message = pair.second
                if (tracks?.isEmpty() == true) {
                    //_uiState.value = FavouriteTracksScreenState.Empty(context.getString(R.string.no_data_in_media_library))
                    _uiState.value = FavouriteTracksScreenState.Empty(message ?: "")
                } else {
                    _uiState.value = FavouriteTracksScreenState.Content(tracks!!)
                }
            }
        }
    }
}