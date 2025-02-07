package com.example.playlistmaker.data.convertors

import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist

class PlaylistConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistTitle = playlist.playlistTitle,
            playListDescription = playlist.playListDescription,
            coverPath = playlist.coverPath,
            trackList = playlist.trackList
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val tracksCount = playlistEntity.trackList?.split(",")?.count() ?: 0
        return Playlist(
            playlistEntity.id,
            playlistEntity.playlistTitle,
            playlistEntity.playListDescription,
            playlistEntity.coverPath,
            playlistEntity.trackList,
            tracksCount
        )
    }
}