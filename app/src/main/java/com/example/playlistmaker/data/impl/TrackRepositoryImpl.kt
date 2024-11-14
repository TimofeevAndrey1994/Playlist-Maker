package com.example.playlistmaker.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.track_local_storage.TracksLocalStorage
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.utils.tryToLong
import java.text.SimpleDateFormat
import java.util.Locale

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksLocalStorage: TracksLocalStorage,
    private val context: Context
) : TracksRepository {
    override fun getTrackFromLocalStorageById(trackId: Long): Track? {
        return tracksLocalStorage.getTrackFromLocalStorageById(trackId)
    }

    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(ItunesRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.Error(context.getString(R.string.error_connection))
            200 -> {
                val trackList = (response as ItunesResponse).results.map {
                    var trackTime = ""
                    val trackDuration = it.trackTime.tryToLong()
                    if (trackDuration > -1)
                        trackTime =
                            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)
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
                val message = if (trackList.isEmpty()) context.getString(R.string.no_data) else null
                Resource.Success(trackList, message)
            }
            else -> Resource.Error(context.getString(R.string.server_error))
        }
    }

    override fun saveTrackToLocalStorage(track: Track): ArrayList<Track> {
        return tracksLocalStorage.saveTrackToLocalStorage(track)
    }

    override fun getTracksFromLocalStorage(): List<Track> {
        return tracksLocalStorage.getTracks() ?: emptyList()
    }

    override fun clearLocalStorage() {
        tracksLocalStorage.clearLocalStorage()
    }

}