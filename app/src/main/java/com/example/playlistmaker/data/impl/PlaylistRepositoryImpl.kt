package com.example.playlistmaker.data.impl

import android.content.Context
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.convertors.PlaylistConvertor
import com.example.playlistmaker.data.convertors.TrackConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.internal_storage.InternalStorageManager
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

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
        return dataBase.getPlaylistDao().getAllPlaylists()
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
            emit("Трек уже добавлен в плейлист ${playlistEntity.playlistTitle}")
        } else {
            dataBase.getTrackInPlaylistDao().saveTrackToPlaylist(trackConvertor.map1(trackEntity))
            playlistEntity.trackList =
                if (playlistEntity.trackList == "") trackId.toString() else playlistEntity.trackList + ',' + trackId.toString()
            dataBase.getPlaylistDao().savePlaylist(playlistEntity)
            emit("Добавлено в плейлист ${playlistEntity.playlistTitle}")
        }
    }
}