package com.example.playlistmaker.domain.impl

import android.annotation.SuppressLint
import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediaPlayerInteractorImpl(private val mediaPlayerManager: MediaPlayerManager) :
    MediaPlayerInteractor {

    override fun initialize(
        trackSource: String?,
        initializeConsumer: MediaPlayerInteractor.InitializeConsumer,
        completeConsumer: MediaPlayerInteractor.CompleteConsumer
    ) {

        mediaPlayerManager.init(trackSource, object : MediaPlayerManager.InitializePlayerConsumer {
            override fun consume() {
                initializeConsumer.consume()
            }
        },
            object : MediaPlayerManager.CompletePlayerConsumer {
                override fun consume() {
                    completeConsumer.consume()
                }
            })
    }

    @SuppressLint("DefaultLocale")
    override fun trackTimeInString(): String {
        val seconds = mediaPlayerManager.getCurrentPosition() / 1000L
        return String.format("%02d:%02d", seconds / 60, seconds % 60)
    }

    override fun nextState(
        mediaPlayerState: MediaPlayerState,
        playConsumer: MediaPlayerInteractor.PlayConsumer,
        pauseConsumer: MediaPlayerInteractor.PauseConsumer
    ): Flow<MediaPlayerState> = flow {
        if (mediaPlayerState == MediaPlayerState.STATE_DEFAULT) {
            emit(MediaPlayerState.STATE_DEFAULT)
        }
        val nextState = mediaPlayerState.getNextState()
        nextState.let {
            if (it == MediaPlayerState.STATE_PLAYING) {
                mediaPlayerManager.play()
                playConsumer.consume()
            } else if (it == MediaPlayerState.STATE_PAUSED) {
                mediaPlayerManager.pause()
                pauseConsumer.consume()
            }
            emit(nextState)
        }
    }

    override fun clear() {
        mediaPlayerManager.clear()
    }

}