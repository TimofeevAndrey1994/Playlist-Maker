package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun getTrackFromLocalStorageById(trackId: Long): Flow<Track?>

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun saveTrackToLocalStorage(track: Track)

    fun clearLocalStorage()

    fun getTracksFromLocalStorage(): Flow<List<Track>>
}