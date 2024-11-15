package com.example.playlistmaker.domain.api

import com.example.playlistmaker.utils.MediaPlayerState

interface MediaPlayerInteractor {

    fun initialize(
        trackSource: String?,
        initializeConsumer: InitializeConsumer,
        completeConsumer: CompleteConsumer
    )

    fun nextState(
        mediaPlayerState: MediaPlayerState,
        updateTimeConsumer: UpdateTimeConsumer
    ): MediaPlayerState

    fun clear()

    interface UpdateTimeConsumer {
        fun consume(time: Int)
    }

    interface InitializeConsumer {
        fun consume()
    }

    interface CompleteConsumer {
        fun consume()
    }
}