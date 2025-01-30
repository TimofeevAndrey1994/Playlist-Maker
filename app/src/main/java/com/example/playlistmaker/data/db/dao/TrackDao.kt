package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert
    suspend fun saveTrackToFavouriteTable(trackEntity: TrackEntity)
    @Delete
    suspend fun deleteTrackFromFavouriteTable(trackEntity: TrackEntity)
    @Query("SELECT * FROM FAVOURITE_TRACK_TABLE ORDER BY addedDate DESC")
    fun getFavouriteTracks(): Flow<List<TrackEntity>>
    @Query("SELECT * FROM FAVOURITE_TRACK_TABLE WHERE trackId=:trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?
}