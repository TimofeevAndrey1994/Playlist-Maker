package com.example.playlistmaker.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.Date

@Parcelize
data class Song(
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
): Parcelable
