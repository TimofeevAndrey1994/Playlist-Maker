package com.example.playlistmaker.data.convertors

import android.annotation.SuppressLint
import com.example.playlistmaker.data.db.entities.TrackEntity
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import com.example.playlistmaker.domain.model.Track

class TrackConvertor {
    fun map(track: Track): TrackEntity{

        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.previewUrl,
            track.artworkUrl100,
            track.country,
            track.releaseDate,
            track.collectionName,
            track.primaryGenreName,
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun map(track: TrackEntity): Track{
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.previewUrl,
            track.artworkUrl100,
            track.country,
            track.releaseDate,
            track.collectionName,
            track.primaryGenreName
        )
    }

    fun mapToTrackInPlaylistEntity(track: TrackEntity): TrackInPlaylistEntity{
        return TrackInPlaylistEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTime,
            track.previewUrl,
            track.artworkUrl100,
            track.country,
            track.releaseDate,
            track.collectionName,
            track.primaryGenreName
        )
    }

}