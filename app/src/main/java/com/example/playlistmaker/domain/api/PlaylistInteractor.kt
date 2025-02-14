package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun savePlaylistToDb(playlist: Playlist)

    fun getPlaylistsFromDb(): Flow<Pair<List<Playlist>?, String?>>

    fun addTrackToPlaylist(track: Track, playlistId: Int): Flow<String?>

}