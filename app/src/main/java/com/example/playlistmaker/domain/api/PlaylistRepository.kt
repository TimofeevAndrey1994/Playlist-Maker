package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun savePlaylistToDb(playlist: Playlist)
    fun getAllPlaylists(): Flow<Resource<List<Playlist>>>
}