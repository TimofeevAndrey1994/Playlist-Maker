package com.example.playlistmaker.ui.playlist_details.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(playlistId: Int, private val playlistInteractor: PlaylistInteractor): ViewModel() {
    private val _currentPlaylist: MutableLiveData<Playlist> = MutableLiveData()
    fun currentPlayList(): LiveData<Playlist> = _currentPlaylist

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylistFromDb(playlistId).collect { playlist ->
                _currentPlaylist.postValue(playlist)
            }
        }
    }
}