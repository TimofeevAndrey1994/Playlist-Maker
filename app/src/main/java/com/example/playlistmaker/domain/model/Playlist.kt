package com.example.playlistmaker.domain.model


data class Playlist(
    val id: Long = 0,
    val playlistTitle: String,
    val playListDescription: String? = null,
    var coverPath: String? = null,
    val trackList: String? = null,
    val tracksCount: Int = 0
)