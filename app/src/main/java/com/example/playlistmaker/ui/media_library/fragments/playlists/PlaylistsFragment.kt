package com.example.playlistmaker.ui.media_library.fragments.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_library.state.ScreenState
import com.example.playlistmaker.ui.media_library.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.media_library.recycler_view.PlaylistAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BaseFragmentBinding<FragmentPlaylistsBinding>() {

    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private val playlistAdapter = PlaylistAdapter()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistsBinding {
        return FragmentPlaylistsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            btnCreateNewPlaylist.setOnClickListener{
                findNavController().navigate(R.id.action_rootMediaLibraryFragment_to_detailsPlaylistFragment)
            }
            rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
            rvPlaylists.adapter = playlistAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                playlistsViewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }

    }

    private fun render(state: ScreenState<Playlist>){
        with(binding) {
            when(state){
                is ScreenState.Content -> {
                    ivNoData.isVisible = false
                    tvNoData.isVisible = false
                    rvPlaylists.isVisible = true
                    playlistAdapter.addAll(state.list)
                }
                is ScreenState.Empty -> {
                    ivNoData.isVisible = true
                    tvNoData.isVisible = true
                    rvPlaylists.isVisible = false
                    tvNoData.text = state.message
                }
                is ScreenState.Loading -> {
                    ivNoData.isVisible = false
                    tvNoData.isVisible = false
                    rvPlaylists.isVisible = false
                }
            }
        }

    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}