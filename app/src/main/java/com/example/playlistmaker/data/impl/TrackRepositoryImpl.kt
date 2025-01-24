package com.example.playlistmaker.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.data.convertors.TrackConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.dto.ItunesRequest
import com.example.playlistmaker.data.dto.ItunesResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.track_local_storage.api.TracksLocalStorage
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.utils.tryToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackRepositoryImpl(
    private val networkClient: NetworkClient,
    private val tracksLocalStorage: TracksLocalStorage,
    private val context: Context,
    private val dataBase: AppDataBase,
    private val trackConvertor: TrackConvertor
) : TracksRepository {

    override fun getTrackFromLocalStorageById(trackId: Long): Flow<Track?> {
        return flow {
            var isFavouriteRes = false
            var track = tracksLocalStorage.getTrackFromLocalStorageById(trackId)
            val trackEntity = dataBase.getTrackDao().getTrackById(trackId)
            trackEntity?.let { isFavouriteRes = true }
            if (track == null) {
                trackEntity?.let { track = trackConvertor.map(trackEntity) }
            }
            emit(track.apply { track?.isFavourite = isFavouriteRes })
        }.flowOn(Dispatchers.IO)
    }

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(ItunesRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error(context.getString(R.string.error_connection)))
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
                emit(Resource.Success(trackList, message))
            }

            else -> emit(Resource.Error(context.getString(R.string.server_error)))
        }
    }

    override suspend fun saveTrackToLocalStorage(track: Track) {
        tracksLocalStorage.saveTrackToLocalStorage(track)
    }

    override fun getTracksFromLocalStorage(): Flow<List<Track>> = flow {
        emit(tracksLocalStorage.getTracks() ?: emptyList())
    }

    override suspend fun clearLocalStorage() {
        tracksLocalStorage.clearLocalStorage()
    }

    override fun getAllFavouriteTracks(): Flow<Resource<List<Track>>> {
        return dataBase.getTrackDao().getFavouriteTracks()
            .map { it.map { track -> trackConvertor.map(track) } }
            .transform { tracks -> emit(Resource.Success(tracks, context.getString(R.string.no_data_in_media_library))) }
    }

    override suspend fun saveTrackToFavouriteTable(track: Track) {
        dataBase.getTrackDao()
            .saveTrackToFavouriteTable(trackConvertor.map(track).apply { addedDate = Date().time })
    }

    override suspend fun deleteTrackFromFavouriteTable(track: Track) {
        dataBase.getTrackDao().deleteTrackFromFavouriteTable(trackConvertor.map(track))
    }

}