package com.example.playlistmaker.ui.playlist_details.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistId: Int?, private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    private val _currentPlaylist = MutableLiveData<Playlist>()
    fun currentPlaylist(): LiveData<Playlist> = _currentPlaylist

    private val _showToast = MutableSharedFlow<String>()
    val showToast: SharedFlow<String> = _showToast.asSharedFlow()

    init {
        viewModelScope.launch {
            if ((playlistId != null) and (playlistId != 0)){
                playlistInteractor.getPlaylistFromDb(playlistId!!).collect { playlist ->
                    _currentPlaylist.postValue(playlist!!)
                }
            }
        }
    }

    fun createOrUpdatePlaylist(title: String, description: String?, imageUri: Uri?) {
        viewModelScope.launch {
            _showToast.emit(String.format(PLAYLIST_ALREADY_WAS_CREATED, title))
            playlistInteractor.savePlaylistToDb(
                Playlist(
                    id = playlistId ?: 0,
                    playlistTitle = title,
                    playListDescription = description,
                    coverPath = imageUri.toString(),
                    trackList = _currentPlaylist.value?.trackList
                )
            )
        }
    }

    companion object {
        private const val PLAYLIST_ALREADY_WAS_CREATED = "Плейлист %s был создан!"
    }
}