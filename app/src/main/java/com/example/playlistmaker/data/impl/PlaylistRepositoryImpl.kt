package com.example.playlistmaker.data.impl

import android.content.Context
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.convertors.PlaylistConvertor
import com.example.playlistmaker.data.convertors.TrackConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import com.example.playlistmaker.data.internal_storage.InternalStorageManager
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.utils.tryToLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val dataBase: AppDataBase,
    private val playlistConvertor: PlaylistConvertor,
    private val trackConvertor: TrackConvertor,
    private val internalStorageManager: InternalStorageManager,
    private val context: Context
) : PlaylistRepository {

    override suspend fun savePlaylistToDb(playlist: Playlist) {
        val imageUri = playlist.coverPath
        if ((imageUri != null) && (imageUri != "null")) {
            val uri = internalStorageManager.saveImageToInternalStorage(imageUri.toUri())
            playlist.coverPath = uri.toString()
        }
        dataBase.getPlaylistDao().savePlaylist(playlistConvertor.map(playlist))
    }

    override fun getAllPlaylists(): Flow<Resource<List<Playlist>>> {
        return dataBase.getPlaylistDao().getAllPlaylistsFlow()
            .map { it.map { playlist -> playlistConvertor.map(playlist) } }
            .transform { playlists ->
                emit(
                    Resource.Success(
                        playlists,
                        context.getString(R.string.you_didnt_create_playlists)
                    )
                )
            }
    }

    override fun addTrackToPlaylist(track: Track, playlistId: Int): Flow<String?> = flow {
        val playlistEntity = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        val trackEntity = trackConvertor.map(track)
        val trackId = track.trackId
        val trackList = if (playlistEntity.trackList == "") "" else ",${playlistEntity.trackList},"
        if (trackList.contains(",$trackId,")) {
            emit(
                context.getString(R.string.track_already_added_to_playlist)
                    .format(playlistEntity.playlistTitle)
            )
        } else {
            dataBase.getTrackInPlaylistDao()
                .saveTrackToPlaylist(trackConvertor.mapToTrackInPlaylistEntity(trackEntity))
            playlistEntity.trackList =
                if (playlistEntity.trackList == "") trackId.toString() else playlistEntity.trackList + ',' + trackId.toString()
            dataBase.getPlaylistDao().savePlaylist(playlistEntity)
            emit(context.getString(R.string.added_to_playlist).format(playlistEntity.playlistTitle))
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Long, playlistId: Int) {
        val playlistEntity: PlaylistEntity?
        withContext(Dispatchers.IO) {
            playlistEntity = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        }

        var trackList =
            if (playlistEntity?.trackList == null) "" else "," + playlistEntity.trackList + ","

        trackList = trackList.replace(",$trackId,", ",")
        trackList = trackList.removeSurrounding(",", ",")
        if (trackList == ",") trackList = ""

        playlistEntity?.trackList = trackList

        withContext(Dispatchers.IO) {
            dataBase.getPlaylistDao().savePlaylist(playlistEntity!!)
        }
        checkAndDeleteTrackFromAllPlaylists(trackId)
    }

    override fun getPlaylistFromDb(playlistId: Int): Flow<Playlist> = flow {
        val playlist = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        emit(playlistConvertor.map(playlist))
    }

    override fun getAllTracksFromPlaylist(playlistId: Int): Flow<List<Track>?> {
        return dataBase.getPlaylistDao().getAllTracksFromPlaylist(playlistId)
            .transform { trackList ->
                if ((trackList != "") and (trackList != null)) {
                    val trackArrayList: ArrayList<Track> = ArrayList()
                    trackList?.split(",")?.forEach { trackId ->
                        var trackInPlaylistEntity: TrackInPlaylistEntity?
                        withContext(Dispatchers.IO) {
                            trackInPlaylistEntity =
                                dataBase.getTrackInPlaylistDao().getTrackById(trackId.toLong())
                        }
                        if (trackInPlaylistEntity != null) {
                            trackArrayList.add(trackConvertor.mapToTrack(trackInPlaylistEntity!!))
                        }
                    }
                    emit(trackArrayList)
                } else {
                    emit(emptyList())
                }
            }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val trackList = playlist.trackList?.split(",") ?: emptyList()
        withContext(Dispatchers.IO) {
            dataBase.getPlaylistDao().deletePlaylistById(playlist.id)
        }

        trackList.forEach {
            val trackId = it.tryToLong()
            if (trackId > 0) {
                checkAndDeleteTrackFromAllPlaylists(trackId)
            }
        }
    }

    private suspend fun checkAndDeleteTrackFromAllPlaylists(trackId: Long) {
        var isTrackExist = false
        //--- Если трека нет ни в одном плейлисте, то удаляем его из таблицы в бд
        val playlists = dataBase.getPlaylistDao().getAllPlaylists()
        for (playlist in playlists) {
            val trackListInEachPlaylist = "," + playlist.trackList + ","
            isTrackExist = trackListInEachPlaylist.contains(",$trackId,")
            if (isTrackExist) {
                break
            }
        }
        withContext(Dispatchers.IO) {
            if (!isTrackExist) {
                val trackInPlaylistEntity =
                    dataBase.getTrackInPlaylistDao().getTrackById(trackId)
                dataBase.getTrackInPlaylistDao().deleteTrack(trackInPlaylistEntity)
            }
        }
    }
}