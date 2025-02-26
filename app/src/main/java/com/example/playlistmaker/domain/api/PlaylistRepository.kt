package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun savePlaylistToDb(playlist: Playlist)
    fun getAllPlaylists(): Flow<Resource<List<Playlist>>>
    fun addTrackToPlaylist(track: Track, playlistId: Int): Flow<String?>
    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int)
    suspend fun getPlaylistFromDb(playlistId: Int): Flow<Playlist?>
    fun getAllTracksFromPlaylist(playlistId: Int): Flow<List<Track>?>
    suspend fun deletePlaylist(playlist: Playlist)
}