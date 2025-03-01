package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaylist(playlistEntity: PlaylistEntity)
    @Query("DELETE FROM playlist_table WHERE id=:playlistId")
    suspend fun deletePlaylistById(playlistId: Int)
    @Query("SELECT * FROM PLAYLIST_TABLE")
    fun getAllPlaylistsFlow(): Flow<List<PlaylistEntity>>
    @Query("SELECT * FROM PLAYLIST_TABLE")
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    @Query("SELECT * FROM PLAYLIST_TABLE WHERE id=:playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity
    @Query("SELECT * FROM PLAYLIST_TABLE WHERE id=:playlistId")
    fun getPlaylistByIdFlow(playlistId: Int): Flow<PlaylistEntity?>
    @Query("SELECT trackList FROM PLAYLIST_TABLE WHERE id=:playlistId")
    fun getAllTracksFromPlaylist(playlistId: Int): Flow<String?>
}