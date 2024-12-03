package com.example.playlistmaker.data.impl

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerManager

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

    override fun getCurrentPosition(): Int {
        if (!mediaPlayer.isPlaying) {
            return -1
        }
        return mediaPlayer.currentPosition
    }

    override fun clear() {
        mediaPlayer.reset()
    }
}