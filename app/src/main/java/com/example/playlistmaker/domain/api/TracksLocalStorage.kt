package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Track

interface TracksLocalStorage {

    fun saveTracks(tracks: ArrayList<Track>)

    fun getTracks(): List<Track>?
}