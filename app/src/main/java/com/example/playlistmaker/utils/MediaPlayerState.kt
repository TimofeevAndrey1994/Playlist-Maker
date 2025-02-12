package com.example.playlistmaker.utils

enum class MediaPlayerState {
    STATE_DEFAULT,
    STATE_PREPARED,
    STATE_PLAYING,
    STATE_PAUSED;

    fun getNextState(): MediaPlayerState = when (this) {
        STATE_DEFAULT -> STATE_PREPARED
        STATE_PREPARED -> STATE_PLAYING
        STATE_PLAYING -> STATE_PAUSED
        STATE_PAUSED -> STATE_PLAYING
    }
}