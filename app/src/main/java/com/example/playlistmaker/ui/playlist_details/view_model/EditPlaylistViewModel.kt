package com.example.playlistmaker.ui.playlist_details.view_model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.model.Playlist
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class EditPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _showToast = MutableSharedFlow<String>()
    val showToast: SharedFlow<String> = _showToast.asSharedFlow()

    fun createPlaylist(title: String, description: String?, imageUri: Uri?) {
        viewModelScope.launch {
            _showToast.emit("Плейлист $title был создан!")
            playlistInteractor.savePlaylistToDb(
                Playlist(
                    playlistTitle = title,
                    playListDescription = description,
                    coverPath = imageUri.toString()
                )
            )
        }
    }
}