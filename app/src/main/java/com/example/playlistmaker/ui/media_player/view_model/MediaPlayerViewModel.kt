package com.example.playlistmaker.ui.media_player.view_model

import android.annotation.SuppressLint
import android.app.Application
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media_player.player_state.MediaPlayerState
import org.koin.core.component.KoinComponent

class MediaPlayerViewModel(trackId: Long) : ViewModel(), KoinComponent {

    private var mediaPlayer = MediaPlayer()

    private val currentTrack = MutableLiveData<Track>(null)
    fun  observeCurrentTrack(): LiveData<Track> = currentTrack

    private var currentTrackTime = MutableLiveData<String>()
    fun observeCurrentTrackTime():LiveData<String> = currentTrackTime

    init {
        val tracksInteractor = getKoin().get<TracksInteractor>()
        val track = tracksInteractor.getTrackFromLocalStorageById(trackId)
        currentTrack.postValue(track)
        initMediaPlayer(track?.previewUrl)
    }

    private val mediaPlayerState = MutableLiveData(MediaPlayerState.STATE_DEFAULT)
    fun observeMediaPlayerState(): LiveData<MediaPlayerState> = mediaPlayerState

    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val updateTimeRunnable: Runnable = object : Runnable {
        @SuppressLint("DefaultLocale")
        override fun run() {
            val seconds = mediaPlayer.currentPosition / 1000L
            currentTrackTime.postValue(String.format("%02d:%02d", seconds / 60, seconds % 60))
            mainThreadHandler.postDelayed(this, 500)
        }

    }

    private fun initMediaPlayer(src: String?) {
        if (src == null) {
            return
        }
        mediaPlayer.setDataSource(src)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            resetPlayer()
        }
    }

    private fun stopUpdateTime() {
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
    }

    private fun startUpdateTime() {
        mainThreadHandler.post(updateTimeRunnable)
    }

    private fun resetPlayer() {
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
        mediaPlayerState.postValue(MediaPlayerState.STATE_PREPARED)
    }

    fun nextState(){
        if (mediaPlayerState.value == MediaPlayerState.STATE_DEFAULT) {
            return
        }
        val nextState = mediaPlayerState.value?.getNextState()
        nextState?.let {
            if (it == MediaPlayerState.STATE_PLAYING) {
                mediaPlayer.start()
                startUpdateTime()
            }
            else if (it == MediaPlayerState.STATE_PAUSED){
                mediaPlayer.pause()
                stopUpdateTime()
            }
            mediaPlayerState.postValue(nextState)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
    }
}