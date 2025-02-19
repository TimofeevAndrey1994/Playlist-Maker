package com.example.playlistmaker.ui.playlist_details.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(private val playlistId: Int, private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val _currentPlaylist: MutableLiveData<Playlist> = MutableLiveData()
    fun currentPlayList(): LiveData<Playlist> = _currentPlaylist

    private val _trackListInPlaylist = MutableStateFlow<Pair<List<Track>, Int>>(Pair(emptyList(), 0))
    val trackListInPlaylist: StateFlow<Pair<List<Track>, Int>> = _trackListInPlaylist

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylistFromDb(playlistId).collect { playlist ->
                _currentPlaylist.postValue(playlist)
            }
            playlistInteractor.getAllTracksFromPlaylist(playlistId).collect { pair ->
                _trackListInPlaylist.value = pair
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track){
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track.trackId, _currentPlaylist.value!!.id)
        }
    }
}