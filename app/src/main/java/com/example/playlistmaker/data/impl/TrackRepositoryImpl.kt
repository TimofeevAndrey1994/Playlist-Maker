package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.api.TracksLocalStorage
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.tryToLong
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(private val networkClient: NetworkClient,
                          private val tracksLocalStorage: TracksLocalStorage
): TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(ItunesRequest(expression))
        if (response.resultCode == 200) {
            return (response as ItunesResponse).results.map {
                var trackTime = ""
                val trackDuration = it.trackTime.tryToLong()
                if (trackDuration > -1)
                    trackTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    trackTime,
                    it.previewUrl,
                    it.artworkUrl100,
                    it.country,
                    it.releaseDate,
                    it.collectionName,
                    it.primaryGenreName
                )
            }
        }
        else {
            return emptyList()
        }
    }

    override fun saveTracksToLocalStorage(tracks: ArrayList<Track>) {
        tracksLocalStorage.saveTracks(tracks)
    }

    override fun getTracksFromLocalStorage(): List<Track> {
       return tracksLocalStorage.getTracks() ?: emptyList()
    }

}