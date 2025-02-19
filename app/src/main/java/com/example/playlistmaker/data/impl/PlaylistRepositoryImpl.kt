package com.example.playlistmaker.data.impl

import android.content.Context
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.convertors.PlaylistConvertor
import com.example.playlistmaker.data.convertors.TrackConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import com.example.playlistmaker.data.internal_storage.InternalStorageManager
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
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

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlistId: Int) {
        val playlistEntity = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        val trackList = "," + playlistEntity.trackList + ","

        trackList.replace(",$trackId,", ",")
        trackList.removeSurrounding(",", ",")

        var isTrackExist = false
        //--- Если трека нет ни в одном плейлисте, то удаляем его из таблицы в бд
        val playlists = dataBase.getPlaylistDao().getAllPlaylists()
        for (playlist in playlists) {
            if (playlist.id != playlistId) {
                val trackListInEachPlaylist = "," + playlist.trackList + ","
                isTrackExist = trackListInEachPlaylist.contains(",$trackId,")
            }
            if (isTrackExist) {
                break
            }
        }
        if (!isTrackExist) {
            val trackInPlaylistEntity =
                dataBase.getTrackInPlaylistDao().getTrackById(trackId.toLong())
            dataBase.getTrackInPlaylistDao().deleteTrack(trackInPlaylistEntity)
        }
    }

    override fun getPlaylistFromDb(playlistId: Int): Flow<Playlist> = flow {
        val playlist = dataBase.getPlaylistDao().getPlaylistById(playlistId)
        emit(playlistConvertor.map(playlist))
    }

    override fun getAllTracksFromPlaylist(playlistId: Int): Flow<List<Track>>{
        return dataBase.getPlaylistDao().getAllTracksFromPlaylist(playlistId)
            .transform { trackList ->
                val trackArrayList: ArrayList<Track> = ArrayList()
                trackList.split(",").forEach { trackId ->
                    var trackInPlaylistEntity: TrackInPlaylistEntity?
                    withContext(Dispatchers.IO){
                        trackInPlaylistEntity = dataBase.getTrackInPlaylistDao().getTrackById(trackId.toLong())
                    }
                    if (trackInPlaylistEntity != null) {
                        trackArrayList.add(trackConvertor.mapToTrack(trackInPlaylistEntity!!))
                    }
                }
                emit(trackArrayList)
            }
    }
}