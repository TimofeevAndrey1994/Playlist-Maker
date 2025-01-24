package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun getTrackFromLocalStorageById(trackId: Long): Flow<Track?>

    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    suspend fun saveTrackToLocalStorage(track: Track)

    suspend fun clearLocalStorage()

    fun getTracksFromLocalStorage(): Flow<List<Track>>

    suspend fun saveTrackToFavouriteDb(track: Track)

    suspend fun deleteTrackFromFavouriteTable(track: Track)

    fun getFavouriteTracks(): Flow<Pair<List<Track>?, String?>>
}