package com.example.playlistmaker.data.impl

import android.content.Context
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.data.convertors.PlaylistConvertor
import com.example.playlistmaker.data.db.AppDataBase
import com.example.playlistmaker.data.internal_storage.InternalStorageManager
import com.example.playlistmaker.domain.api.PlaylistRepository
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

class PlaylistRepositoryImpl(
    private val dataBase: AppDataBase,
    private val playlistConvertor: PlaylistConvertor,
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
}