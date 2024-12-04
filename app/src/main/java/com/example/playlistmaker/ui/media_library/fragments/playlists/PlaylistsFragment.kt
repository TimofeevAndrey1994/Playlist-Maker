package com.example.playlistmaker.ui.media_library.fragments.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.ui.media_library.fragments.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_library.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BaseFragmentBinding<FragmentPlaylistsBinding>() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}