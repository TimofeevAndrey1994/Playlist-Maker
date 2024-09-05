package com.example.playlistmaker.model

import com.google.gson.annotations.SerializedName

data class Song(
    val trackId: Long,
    val trackName:  String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime:  String,
    val artworkUrl100: String? = ""
)
