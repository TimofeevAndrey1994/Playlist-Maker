package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    override fun getTrackFromLocalStorageById(trackId: Long): Flow<Track?> {
        return repository.getTrackFromLocalStorageById(trackId)
    }

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Error -> Pair(null, result.message)
                is Resource.Success -> Pair(result.data, result.message)
            }
        }
    }

    override suspend fun saveTrackToLocalStorage(track: Track) {
        repository.saveTrackToLocalStorage(track)
    }

    override suspend fun clearLocalStorage() {
        repository.clearLocalStorage()
    }

    override fun getTracksFromLocalStorage(): Flow<List<Track>> {
        return repository.getTracksFromLocalStorage()
    }

    override suspend fun saveTrackToFavouriteDb(track: Track) {
        repository.saveTrackToFavouriteTable(track)
    }

    override suspend fun deleteTrackFromFavouriteTable(track: Track) {
        repository.deleteTrackFromFavouriteTable(track)
    }

    override fun getFavouriteTracks(): Flow<Pair<List<Track>?, String?>> {
        return repository.getAllFavouriteTracks().map { result ->
                when(result){
                    is Resource.Error -> Pair(null, null)
                    is Resource.Success -> Pair(result.data, result.message)
                }
            }
    }
}