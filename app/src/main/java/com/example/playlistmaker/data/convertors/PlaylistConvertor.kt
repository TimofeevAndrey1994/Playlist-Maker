package com.example.playlistmaker.data.convertors

import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist

class PlaylistConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistTitle = playlist.playlistTitle,
            playListDescription = playlist.playListDescription,
            coverPath = playlist.coverPath,
            trackList = ""
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