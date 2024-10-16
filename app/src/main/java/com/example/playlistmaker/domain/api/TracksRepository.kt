package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksRepository {

    fun searchTracks(expression: String): List<Track>

    fun saveTracksToLocalStorage(tracks: ArrayList<Track>)

    fun getTracksFromLocalStorage(): List<Track>

}