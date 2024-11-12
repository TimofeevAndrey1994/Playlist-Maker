package com.example.playlistmaker.data.track_local_storage

import com.example.playlistmaker.domain.model.Track

interface TracksLocalStorage {

    fun getTrackFromLocalStorageById(trackId: Long): Track?

    fun saveTrackToLocalStorage(track: Track): ArrayList<Track>

    fun getTracks(): List<Track>?

    fun clearLocalStorage()
}