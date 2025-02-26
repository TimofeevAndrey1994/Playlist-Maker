package com.example.playlistmaker.data.convertors

import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist

class PlaylistConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            playlistTitle = playlist.playlistTitle,
            playListDescription = if (playlist.playListDescription == "") null else playlist.playListDescription,
            coverPath = playlist.coverPath,
            trackList = playlist.trackList ?: ""
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val tracksCount = if (playlistEntity.trackList == "") 0 else playlistEntity.trackList.split(",").count()
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