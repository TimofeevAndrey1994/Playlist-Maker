package com.example.playlistmaker.ui.media_player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media_library.state.ScreenState
import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    trackId: Long,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val tracksInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var updateTimeJob: Job? = null

    private val currentTrack = MutableLiveData<Track?>(null)
    fun observeCurrentTrack(): LiveData<Track?> = currentTrack

    private var currentTrackTime = MutableLiveData<String>()
    fun observeCurrentTrackTime(): LiveData<String> = currentTrackTime

    private val isFavouriteTrack = MutableLiveData(false)
    fun observeIsFavouriteTrack(): LiveData<Boolean> = isFavouriteTrack

    private val _uiState =  MutableStateFlow<ScreenState<Playlist>>(ScreenState.Loading())
    val uiState: StateFlow<ScreenState<Playlist>> = _uiState

    private val _showToast = MutableSharedFlow<String>()
    val showToast: SharedFlow<String> = _showToast.asSharedFlow()

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

    private val playConsumer = MediaPlayerInteractor.PlayConsumer {
        updateTimeJob?.cancel()
        updateTimeJob = viewModelScope.launch {
            while (true) {
                delay(300L)
                currentTrackTime.postValue(mediaPlayerInteractor.trackTimeInString())
            }
        }
    }
    private val pauseConsumer = MediaPlayerInteractor.PauseConsumer { updateTimeJob?.cancel() }


    private val mediaPlayerState = MutableLiveData(MediaPlayerState.STATE_DEFAULT)
    fun observeMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    init {
        viewModelScope.launch {
            val foundTrack = async {
                var res: Track? = null
                tracksInteractor.getTrackFromLocalStorageById(trackId)
                    .collect { track ->
                        if (track != null) {
                            res = track
                        }
                    }
                return@async res
            }

            currentTrack.value = foundTrack.await()

            mediaPlayerInteractor.initialize(foundTrack.await()?.previewUrl,
                initializeConsumer = { mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED) },
                completeConsumer = {
                    mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
                    updateTimeJob?.cancel()
                })
            isFavouriteTrack.postValue(foundTrack.await()?.isFavourite)
        }
    }

    fun nextState() {
        if (mediaPlayerState.value == MediaPlayerState.STATE_DEFAULT) {
            return
        }
        viewModelScope.launch {
            mediaPlayerInteractor.nextState(mediaPlayerState.value!!, playConsumer, pauseConsumer)
                .collect { value ->
                    mediaPlayerState.postValue(value)
                }
        }
    }

    fun onLike() {
        viewModelScope.launch {
            val track = currentTrack.value
            track?.let {
                if (track.isFavourite) {
                    tracksInteractor.deleteTrackFromFavouriteTable(track)
                    currentTrack.value?.isFavourite = false
                    isFavouriteTrack.postValue(false)
                } else {
                    tracksInteractor.saveTrackToFavouriteDb(track)
                    currentTrack.value?.isFavourite = true
                    isFavouriteTrack.postValue(true)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.clear()
    }

    fun addTrackToPlaylist(playListId: Int) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(currentTrack.value!!, playListId).collect{ value ->
                if (value?.isNotEmpty() == true) {
                    _showToast.emit(value)
                }
            }
        }
    }
}