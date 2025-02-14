package com.example.playlistmaker.ui.media_library.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.media_library.state.ScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val _uiState =  MutableStateFlow<ScreenState<Playlist>>(ScreenState.Loading())
    val uiState: StateFlow<ScreenState<Playlist>> = _uiState

    init {
        viewModelScope.launch {
            _uiState.value = ScreenState.Loading()
            playlistInteractor.getPlaylistsFromDb()
                .collect { pair ->
                    val playlists = pair.first
                    val message = pair.second
                    if (playlists?.isEmpty() == true){
                        _uiState.value = ScreenState.Empty(message ?: "")
                    }
                    else {
                        _uiState.value = ScreenState.Content(playlists!!)
                    }
                }
        }
    }
}