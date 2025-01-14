package com.example.playlistmaker.domain.api

import com.example.playlistmaker.utils.MediaPlayerState
import kotlinx.coroutines.flow.Flow

interface MediaPlayerInteractor {

    fun initialize(
        trackSource: String?,
        initializeConsumer: InitializeConsumer,
        completeConsumer: CompleteConsumer,
        playConsumer: PlayConsumer,
        pauseConsumer: PauseConsumer
    )

    fun nextState(mediaPlayerState: MediaPlayerState): Flow<MediaPlayerState>

    suspend fun trackTimeInStringFlowable(): Flow<String>

    fun clear()

    fun interface InitializeConsumer {
        fun consume()
    }

    fun interface CompleteConsumer {
        fun consume()
    }

    fun interface PlayConsumer {
        fun consume()
    }

    fun interface PauseConsumer {
        fun consume()
    }
}
