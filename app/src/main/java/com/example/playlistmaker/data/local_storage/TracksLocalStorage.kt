package com.example.playlistmaker.data.local_storage

import com.example.playlistmaker.domain.model.Track

interface TracksLocalStorage {

    fun saveTrackToLocalStorage(track: Track): ArrayList<Track>

    fun getTracks(): List<Track>?

    fun clearLocalStorage()
}