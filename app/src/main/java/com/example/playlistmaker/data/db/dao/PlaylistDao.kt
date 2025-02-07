package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert
    suspend fun savePlaylist(playlistEntity: PlaylistEntity)
    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)
    @Query("SELECT * FROM PLAYLIST_TABLE")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
}