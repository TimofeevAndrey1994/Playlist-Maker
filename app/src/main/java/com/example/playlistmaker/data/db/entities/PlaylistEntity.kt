package com.example.playlistmaker.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val playlistTitle: String,
    val playListDescription: String?,
    val coverPath: String?,
    @ColumnInfo(defaultValue = "")
    var trackList: String = ""
)
