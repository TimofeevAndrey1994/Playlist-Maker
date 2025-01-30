package com.example.playlistmaker.data.track_local_storage.api

import com.example.playlistmaker.domain.model.Track

interface TracksLocalStorage {

    suspend fun getTrackFromLocalStorageById(trackId: Long): Track?

    suspend fun saveTrackToLocalStorage(track: Track)

    suspend fun getTracks(): List<Track>?

    suspend fun clearLocalStorage()
}