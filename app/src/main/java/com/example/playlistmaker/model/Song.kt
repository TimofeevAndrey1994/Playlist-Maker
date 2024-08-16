package com.example.playlistmaker.model

data class Song(
    val trackName:  String,
    val artistName: String,
    val trackTime:  String,
    val artworkUrl100: String? = ""
)
