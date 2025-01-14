package com.example.playlistmaker.domain.impl

import android.annotation.SuppressLint
import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MediaPlayerInteractorImpl(private val mediaPlayerManager: MediaPlayerManager) : MediaPlayerInteractor {

    private var playConsumer: MediaPlayerInteractor.PlayConsumer? = null
    private var pauseConsumer: MediaPlayerInteractor.PauseConsumer? = null

    override fun initialize(
        trackSource: String?,
        initializeConsumer: MediaPlayerInteractor.InitializeConsumer,
        completeConsumer: MediaPlayerInteractor.CompleteConsumer,
        playConsumer: MediaPlayerInteractor.PlayConsumer,
        pauseConsumer: MediaPlayerInteractor.PauseConsumer
    ) {
        this.playConsumer  = playConsumer
        this.pauseConsumer = pauseConsumer

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
    override suspend fun trackTimeInStringFlowable(): Flow<String> {
        return mediaPlayerManager.getCurrentPositionFlowable()
            .map { time ->
                val seconds = time / 1000L
                String.format("%02d:%02d", seconds / 60, seconds % 60)
            }
    }

    override fun nextState(
        mediaPlayerState: MediaPlayerState): Flow<MediaPlayerState> = flow {
        if (mediaPlayerState == MediaPlayerState.STATE_DEFAULT) {
             emit(MediaPlayerState.STATE_DEFAULT)
        }
        val nextState = mediaPlayerState.getNextState()
        nextState.let {
            if (it == MediaPlayerState.STATE_PLAYING) {
                mediaPlayerManager.play()
                playConsumer?.consume()
            } else if (it == MediaPlayerState.STATE_PAUSED) {
                mediaPlayerManager.pause()
                pauseConsumer?.consume()
            }
            emit(nextState)
        }
    }

    override fun clear() {
        mediaPlayerManager.clear()
    }

}