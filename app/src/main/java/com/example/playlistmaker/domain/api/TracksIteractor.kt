package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun saveTrackToLocalStorage(track: Track): ArrayList<Track>

    fun clearLocalStorage()

    fun getTracksFromLocalStorage(consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(tracks: List<Track>?)
    }
}