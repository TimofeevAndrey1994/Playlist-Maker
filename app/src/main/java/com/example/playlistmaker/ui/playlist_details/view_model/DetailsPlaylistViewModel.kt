package com.example.playlistmaker.ui.playlist_details.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DetailsPlaylistViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _currentPlaylist = MutableStateFlow<Playlist?>(null)
    val currentPlayList: StateFlow<Playlist?> = _currentPlaylist

    private val _trackListInPlaylist =
        MutableStateFlow<Pair<List<Track>?, Int>>(Pair(emptyList(), 0))
    val trackListInPlaylist: StateFlow<Pair<List<Track>?, Int>> = _trackListInPlaylist

    private val _playlistDeleted = MutableSharedFlow<Boolean>()
    val playlistDeleted: SharedFlow<Boolean> = _playlistDeleted.asSharedFlow()

    private val _showToast = MutableSharedFlow<String>()
    val showToast: SharedFlow<String> = _showToast.asSharedFlow()

    init {
        viewModelScope.launch {
            playlistInteractor.getPlaylistFromDb(playlistId).collect { playlist ->
                _currentPlaylist.emit(playlist)
            }
        }
        viewModelScope.launch {
            playlistInteractor.getAllTracksFromPlaylist(playlistId).collect { pair ->
                _trackListInPlaylist.value = pair
            }
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track.trackId, _currentPlaylist.value?.id ?: 0)
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(_currentPlaylist.value!!)
            _playlistDeleted.emit(true)
        }
    }

    fun sharePlaylist() {
        if (_trackListInPlaylist.value.first?.isNotEmpty() == true) {
            val trackListString = _trackListInPlaylist.value.first?.mapIndexed { i, value ->
                "$i. ${value.trackName} - ${value.artistName} ${value.trackTime}"
            }

            playlistInteractor.sharePlaylist(
                _currentPlaylist.value?.playlistTitle ?: "",
                trackListString as ArrayList<String>
            )
        }
        else {
            viewModelScope.launch {
                _showToast.emit("В этом плейлисте нет списка треков, которым можно поделиться")
            }
        }
    }
}