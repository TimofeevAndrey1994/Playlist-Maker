package com.example.playlistmaker.domain.impl

import android.os.Handler
import android.os.SystemClock
import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.domain.api.MediaPlayerInteractor
import com.example.playlistmaker.utils.MediaPlayerState

class MediaPlayerInteractorImpl(
    private val mediaPlayerManager: MediaPlayerManager,
    private val mainThreadHandler: Handler
) : MediaPlayerInteractor {

    private var updateTimeRunnable: Runnable? = null

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

    override fun nextState(
        mediaPlayerState: MediaPlayerState,
        updateTimeConsumer: MediaPlayerInteractor.UpdateTimeConsumer
    ): MediaPlayerState {
        if (mediaPlayerState == MediaPlayerState.STATE_DEFAULT) {
            return MediaPlayerState.STATE_DEFAULT
        }
        val nextState = mediaPlayerState.getNextState()
        nextState.let {
            if (it == MediaPlayerState.STATE_PLAYING) {
                mediaPlayerManager.play()
                updateTimeRunnable = Runnable {
                    updateTimeConsumer.consume(mediaPlayerManager.getCurrentPosition())
                    mainThreadHandler.postAtTime(
                        updateTimeRunnable!!,
                        UPDATE_TIME_TOKEN,
                        SystemClock.uptimeMillis() + 500
                    )
                }
                mainThreadHandler.post(updateTimeRunnable!!)
            } else if (it == MediaPlayerState.STATE_PAUSED) {
                mediaPlayerManager.pause()
                if (updateTimeRunnable != null) {
                    mainThreadHandler.removeCallbacksAndMessages(UPDATE_TIME_TOKEN)
                }
            }
            return nextState
        }
    }

    override fun clear() {
        mediaPlayerManager.clear()
        mainThreadHandler.removeCallbacksAndMessages(UPDATE_TIME_TOKEN)
    }

    companion object {
        private val UPDATE_TIME_TOKEN = Any()
    }

}