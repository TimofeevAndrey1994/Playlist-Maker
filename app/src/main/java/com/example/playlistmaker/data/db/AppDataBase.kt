package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TrackEntity
import com.example.playlistmaker.data.db.type_converter.TypeConverter

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class])
@TypeConverters(TypeConverter::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun getTrackDao(): TrackDao
    abstract fun getPlaylistDao(): PlaylistDao
}