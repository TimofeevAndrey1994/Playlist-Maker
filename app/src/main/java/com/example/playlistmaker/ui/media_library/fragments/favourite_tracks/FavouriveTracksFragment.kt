package com.example.playlistmaker.ui.media_library.fragments.favourite_tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_library.state.FavouriteTracksScreenState
import com.example.playlistmaker.ui.media_library.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.ui.media_player.fragments.MediaPlayerFragment
import com.example.playlistmaker.ui.search.recycler_view.TrackAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriveTracksFragment : BaseFragmentBinding<FragmentFavouriteTracksBinding>() {

    private val favouriteTracksViewModel: FavouriteTracksViewModel by viewModel()
    private val trackAdapter = TrackAdapter()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favouriteTracks.adapter = trackAdapter
        trackAdapter.setOnItemClickListener { openPlayer(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                favouriteTracksViewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: FavouriteTracksScreenState?) {
        with(binding) {
            when (state) {
                is FavouriteTracksScreenState.Content -> {
                    favouriteTracks.isVisible = true
                    emptyState.isVisible = false
                    trackAdapter.addAll(state.trackList)
                }

                is FavouriteTracksScreenState.Empty -> {
                    favouriteTracks.isVisible = false
                    tvEmptyMessage.text = state.message
                    emptyState.isVisible = true
                }

                FavouriteTracksScreenState.Loading -> {
                    favouriteTracks.isVisible = false
                    emptyState.isVisible = false
                }
                null -> {
                    favouriteTracks.isVisible = false
                    emptyState.isVisible = false
                }
            }
        }
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_rootMediaLibraryFragment_to_mediaPlayerFragment,
            MediaPlayerFragment.createArgs(track.trackId)
        )
    }

    companion object {
        fun newInstance(): FavouriveTracksFragment = FavouriveTracksFragment()
    }

}