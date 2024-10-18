package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksRepository {

    fun searchTracks(expression: String): List<Track>?

    fun saveTrackToLocalStorage(track: Track): ArrayList<Track>

    fun getTracksFromLocalStorage(): List<Track>

    fun clearLocalStorage()

}