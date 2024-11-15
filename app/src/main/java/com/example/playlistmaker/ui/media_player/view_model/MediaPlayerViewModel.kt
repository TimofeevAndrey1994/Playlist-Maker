package com.example.playlistmaker.ui.media_player.view_model

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.MediaPlayerState

class MediaPlayerViewModel(
    trackId: Long,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    tracksInteractor: TracksInteractor
) : ViewModel() {


    private val currentTrack = MutableLiveData<Track>(null)
    fun observeCurrentTrack(): LiveData<Track> = currentTrack

    private var currentTrackTime = MutableLiveData<String>()
    fun observeCurrentTrackTime(): LiveData<String> = currentTrackTime

    init {
        val track = tracksInteractor.getTrackFromLocalStorageById(trackId)
        currentTrack.value = track!!
    }

    private val mediaPlayerState = MutableLiveData(MediaPlayerState.STATE_DEFAULT)
    fun observeMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    init {
        mediaPlayerInteractor.initialize(currentTrack.value?.previewUrl,
            object : MediaPlayerInteractor.InitializeConsumer {
                override fun consume() {
                    mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
                }
            },
            object : MediaPlayerInteractor.CompleteConsumer {
                override fun consume() {
                    mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
                }

            })
    }

    fun nextState() {
        if (mediaPlayerState.value == MediaPlayerState.STATE_DEFAULT) {
            return
        }
        mediaPlayerState.value = mediaPlayerInteractor.nextState(
            mediaPlayerState.value!!,
            object : MediaPlayerInteractor.UpdateTimeConsumer {
                @SuppressLint("DefaultLocale")
                override fun consume(time: Int) {
                    Log.d("currentTime", time.toString())
                    val seconds = time / 1000L
                    currentTrackTime.postValue(
                        String.format(
                            "%02d:%02d",
                            seconds / 60,
                            seconds % 60
                        )
                    )
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.clear()
    }
}