package com.example.playlistmaker.domain.api

interface MediaPlayerManager {
    fun init(
        trackSource: String?,
        initializePlayerConsumer: InitializePlayerConsumer,
        completePlayerConsumer: CompletePlayerConsumer
    )

    fun play()
    fun pause()
    fun getCurrentPosition(): Int
    fun clear()

    interface InitializePlayerConsumer {
        fun consume()
    }

    interface CompletePlayerConsumer {
        fun consume()
    }
}