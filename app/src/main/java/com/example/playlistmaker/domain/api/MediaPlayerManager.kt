package com.example.playlistmaker.domain.api

interface MediaPlayerManager {
    fun init(
        trackSource: String?,
        initializePlayerConsumer: InitializePlayerConsumer,
        completePlayerConsumer: CompletePlayerConsumer
    )

    fun play()
    fun pause()
    fun clear()
    fun getCurrentPosition(): Int

    interface InitializePlayerConsumer {
        fun consume()
    }

    interface CompletePlayerConsumer {
        fun consume()
    }
}