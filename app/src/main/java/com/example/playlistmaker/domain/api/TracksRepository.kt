package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {

    fun getTrackFromLocalStorageById(trackId: Long): Track?

    fun searchTracks(expression: String): Resource<List<Track>>

    fun saveTrackToLocalStorage(track: Track): ArrayList<Track>

    fun getTracksFromLocalStorage(): List<Track>

    fun clearLocalStorage()

}