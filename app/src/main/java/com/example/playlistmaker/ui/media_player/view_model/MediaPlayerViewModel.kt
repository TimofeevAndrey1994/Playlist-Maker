package com.example.playlistmaker.ui.media_player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MediaPlayerViewModel(
    trackId: Long,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    private var updateTimeJob: Job? = null

    private val currentTrack = MutableLiveData<Track>(null)
    fun observeCurrentTrack(): LiveData<Track> = currentTrack

    private var currentTrackTime = MutableLiveData<String>()
    fun observeCurrentTrackTime(): LiveData<String> = currentTrackTime

    private val isFavouriteTrack = MutableLiveData(false)
    fun observeIsFavouriteTrack(): LiveData<Boolean> = isFavouriteTrack

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
}