package com.example.playlistmaker.ui.media_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media_library.state.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteTracksViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState<Track>?>(ScreenState.Loading())
    val uiState: StateFlow<ScreenState<Track>?> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = ScreenState.Loading()
            tracksInteractor.getFavouriteTracks().collect { pair ->
                val tracks = pair.first
                val message = pair.second
                if (tracks?.isEmpty() == true) {
                    _uiState.value = ScreenState.Empty(message ?: "")
                } else {
                    _uiState.value = ScreenState.Content(tracks!!)
                }
            }
        }
    }
}