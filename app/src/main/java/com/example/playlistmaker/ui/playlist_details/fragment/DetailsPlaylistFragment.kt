package com.example.playlistmaker.ui.playlist_details.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.playlist_details.view_model.DetailsPlaylistViewModel
import com.example.playlistmaker.ui.search.recycler_view.TrackAdapter
import com.example.playlistmaker.utils.getWordMinuteInCorrectView
import com.example.playlistmaker.utils.getWordTrackInCorrectView
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsPlaylistFragment : BaseFragmentBinding<FragmentPlaylistDetailsBinding>() {

    private val detailsPlaylistViewModel: DetailsPlaylistViewModel by viewModel {
        val playlistId = requireArguments().getInt(PLAYLIST_ID, -1)
        parametersOf(playlistId)
    }

    private val trackAdapter = TrackAdapter()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistDetailsBinding {
        return FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            tvPlaylistDescription.isVisible = false
            detailsPlaylistViewModel.currentPlayList().observe(viewLifecycleOwner) { playlist ->
                Glide.with(root)
                    .load(playlist.coverPath)
                    .placeholder(R.drawable.empty_playlist_cover)
                    .transform(CenterCrop())
                    .into(imageCover)

                tvPlaylistTitle.text = playlist.playlistTitle

                if (playlist.playListDescription?.isNotEmpty() == true) {
                    tvPlaylistDescription.text = playlist.playListDescription
                    tvPlaylistDescription.isVisible = true
                }

                rvTrackList.adapter = trackAdapter
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.trackListInPlaylist.collect { pair ->
                    val trackList = pair.first
                    val duration = pair.second
                    trackAdapter.addAll(trackList)
                    binding.tvPlaylistDurationInMin.text =
                        String.format("%s %s", duration, duration.getWordMinuteInCorrectView())
                    binding.tvPlaylistTracksCount.text = String.format(
                        "%s %s",
                        trackList.count(),
                        trackList.count().getWordTrackInCorrectView()
                    )
                }
            }
        }
    }


    companion object {
        private const val PLAYLIST_ID = "PLAYLIST_ID"

        fun createArgs(playlistId: Int) = bundleOf(PLAYLIST_ID to playlistId)
    }
}