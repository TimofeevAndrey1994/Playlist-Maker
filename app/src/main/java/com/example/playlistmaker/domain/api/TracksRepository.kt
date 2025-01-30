package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun getTrackFromLocalStorageById(trackId: Long): Flow<Track?>

    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    suspend fun saveTrackToLocalStorage(track: Track)

    fun getTracksFromLocalStorage(): Flow<List<Track>>

    suspend fun clearLocalStorage()

    fun getAllFavouriteTracks(): Flow<Resource<List<Track>>>

    suspend fun saveTrackToFavouriteTable(track: Track)

    suspend fun deleteTrackFromFavouriteTable(track: Track)

}