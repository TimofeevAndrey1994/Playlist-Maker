package com.example.playlistmaker.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "favourite_track_table")
class TrackEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName:  String,
    val artistName: String,
    val trackTime:  String,
    @ColumnInfo(defaultValue = "")
    val previewUrl: String?,
    @ColumnInfo(defaultValue = "")
    val artworkUrl100: String?,
    val country: String,
    val releaseDate: Date,
    @ColumnInfo(defaultValue = "")
    val collectionName: String,
    val primaryGenreName: String,
    var addedDate: Long = 0L
)