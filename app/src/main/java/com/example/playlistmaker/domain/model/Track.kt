package com.example.playlistmaker.domain.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Track(
    val trackId: Long,
    val trackName:  String,
    val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime:  String,
    val previewUrl: String? = "",
    val artworkUrl100: String? = "",
    val country: String,
    val releaseDate: Date,
    val collectionName: String = "",
    val primaryGenreName: String,
)
