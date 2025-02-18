package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveTrackToPlaylist(trackInPlaylistEntity: TrackInPlaylistEntity)
    @Query("SELECT * FROM track_in_playlist WHERE trackId=:trackId")
    fun getTrackById(trackId: Long): TrackInPlaylistEntity
    @Delete
    suspend fun deleteTrack(trackInPlaylistEntity: TrackInPlaylistEntity)
}