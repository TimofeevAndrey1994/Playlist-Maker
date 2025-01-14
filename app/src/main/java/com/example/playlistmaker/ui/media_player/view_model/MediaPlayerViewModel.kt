package com.example.playlistmaker.ui.media_player.view_model

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("NullSafeMutableLiveData")
class MediaPlayerViewModel(
    trackId: Long,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    tracksInteractor: TracksInteractor
) : ViewModel() {

    private var updateTimeJob: Job? = null

    private val currentTrack = MutableLiveData<Track>(null)
    fun observeCurrentTrack(): LiveData<Track> = currentTrack

    private var currentTrackTime = MutableLiveData<String>()
    fun observeCurrentTrackTime(): LiveData<String> = currentTrackTime

    init {
        viewModelScope.launch {
            tracksInteractor.getTrackFromLocalStorageById(trackId)
                .collect { track ->
                    if (track != null) {
                        currentTrack.value = track
                    }
                }
        }
    }

    private val mediaPlayerState = MutableLiveData(MediaPlayerState.STATE_DEFAULT)
    fun observeMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    init {
        mediaPlayerInteractor.initialize(currentTrack.value?.previewUrl,
            initializeConsumer = { mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED) },
            completeConsumer = {
                mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
                updateTimeJob?.cancel()
            },
            playConsumer = {
                updateTimeJob = viewModelScope.launch {
                    mediaPlayerInteractor.trackTimeInStringFlowable()
                        .collect { value ->
                            currentTrackTime.postValue(value)
                        }
                }
            },
            pauseConsumer = { updateTimeJob?.cancel() })
    }

    fun nextState() {
        if (mediaPlayerState.value == MediaPlayerState.STATE_DEFAULT) {
            return
        }
        runBlocking {
            mediaPlayerInteractor.nextState(mediaPlayerState.value!!)
                .collect{ value ->
                    mediaPlayerState.postValue(value)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.clear()
    }
}