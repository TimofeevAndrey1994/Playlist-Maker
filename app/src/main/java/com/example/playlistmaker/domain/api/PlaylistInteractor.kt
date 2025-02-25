package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun savePlaylistToDb(playlist: Playlist)

    fun getPlaylistsFromDb(): Flow<Pair<List<Playlist>?, String?>>

    fun addTrackToPlaylist(track: Track, playlistId: Int): Flow<String?>

    fun getPlaylistFromDb(playlistId: Int): Flow<Playlist>

    fun getAllTracksFromPlaylist(playlistId: Int): Flow<Pair<List<Track>?, Int>>

    suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int)

    suspend fun deletePlaylist(playlist: Playlist)

    fun sharePlaylist(playlistName: String, trackListInString: ArrayList<String>)
}