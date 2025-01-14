package com.example.playlistmaker.domain.api

import kotlinx.coroutines.flow.Flow

interface MediaPlayerManager {
    fun init(
        trackSource: String?,
        initializePlayerConsumer: InitializePlayerConsumer,
        completePlayerConsumer: CompletePlayerConsumer
    )

    fun play()
    fun pause()
    fun clear()
    fun getCurrentPositionFlowable(): Flow<Int>

    interface InitializePlayerConsumer {
        fun consume()
    }

    interface CompletePlayerConsumer {
        fun consume()
    }
}