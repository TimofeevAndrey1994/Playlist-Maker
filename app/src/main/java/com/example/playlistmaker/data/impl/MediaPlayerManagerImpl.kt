package com.example.playlistmaker.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediaPlayerManagerImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerManager {

    override fun init(
        trackSource: String?,
        initializePlayerConsumer: MediaPlayerManager.InitializePlayerConsumer,
        completePlayerConsumer: MediaPlayerManager.CompletePlayerConsumer
    ) {
        if (trackSource == null) {
            return
        }

        mediaPlayer.setDataSource(trackSource)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            initializePlayerConsumer.consume()
        }
        mediaPlayer.setOnCompletionListener {
            completePlayerConsumer.consume()
        }
    }

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        if (!mediaPlayer.isPlaying) {
            return
        }
        mediaPlayer.pause()
    }

    override fun clear() {
        mediaPlayer.reset()
    }

    override fun getCurrentPositionFlowable(): Flow<Int> = flow {
        while (true){
            delay(300L)
            emit(mediaPlayer.currentPosition)
        }
    }
}