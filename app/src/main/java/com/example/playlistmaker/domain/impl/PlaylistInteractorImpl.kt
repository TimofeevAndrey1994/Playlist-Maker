package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlaylistInteractor
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {
    override suspend fun savePlaylistToDb(playlist: Playlist) {
        playlistRepository.savePlaylistToDb(playlist)
    }

    override fun getPlaylistsFromDb(): Flow<Pair<List<Playlist>?, String?>> {
        return playlistRepository.getAllPlaylists()
            .map { result ->
                when (result) {
                    is Resource.Success -> {
                        Pair(result.data, result.message)
                    }

                    is Resource.Error -> {
                        Pair(null, null)
                    }
                }
            }
    }

    override fun addTrackToPlaylist(track: Track, playlistId: Int): Flow<String?> {
        return playlistRepository.addTrackToPlaylist(track, playlistId)
    }

    override fun getPlaylistFromDb(playlistId: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistFromDb(playlistId)
    }
}