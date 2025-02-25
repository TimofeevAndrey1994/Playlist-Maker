package com.example.playlistmaker.ui.playlist_details.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_player.fragments.MediaPlayerFragment
import com.example.playlistmaker.ui.playlist_details.view_model.DetailsPlaylistViewModel
import com.example.playlistmaker.ui.search.recycler_view.TrackAdapter
import com.example.playlistmaker.utils.getWordMinuteInCorrectView
import com.example.playlistmaker.utils.getWordTrackInCorrectView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        trackAdapter.setOnItemClickListener { track ->
            findNavController().navigate(
                R.id.action_detailsPlaylistFragment_to_mediaPlayerFragment,
                MediaPlayerFragment.createArgs(track.trackId)
            )
        }

        trackAdapter.setOnItemLongClickListener { track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.wanna_delete_track_from_playlist)
                .setPositiveButton(R.string.to_delete) { _, _ ->
                    detailsPlaylistViewModel.deleteTrackFromPlaylist(track)
                }
                .setNeutralButton(R.string.cancel) { _, _ ->

                }
                .show()
        }

        val bottomSheetBehaviorMenu =
            BottomSheetBehavior.from(binding.playlistMenuBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        bottomSheetBehaviorMenu.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                        binding.playlistsBottomSheet.isVisible = true
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                        binding.playlistsBottomSheet.isVisible = false
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        with(binding) {
            tvPlaylistDescription.isVisible = false
            arrowBackFromDetailsPlaylist.setOnClickListener {
                findNavController().navigateUp()
            }
            ivContextMenu.setOnClickListener {
                bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            ivSharePlaylist.setOnClickListener {
                detailsPlaylistViewModel.sharePlaylist()
            }

            deletePlaylist.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.wanna_delete_playlist)
                    .setNegativeButton(R.string.no) { _, _ ->

                    }
                    .setPositiveButton(R.string.yes) { _, _ ->
                        detailsPlaylistViewModel.deletePlaylist()
                    }
                    .show()
            }

            editPlaylistInformation.setOnClickListener {
                findNavController().navigate(
                    R.id.action_detailsPlaylistFragment_to_editPlaylistFragment,
                    EditPlaylistFragment.createArgs(requireArguments().getInt(PLAYLIST_ID))
                )
            }
            textViewSharePlaylist.setOnClickListener {
                detailsPlaylistViewModel.sharePlaylist()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.trackListInPlaylist.collect { pair ->
                    val trackList = pair.first
                    val duration = pair.second
                    trackAdapter.addAll(trackList ?: emptyList())
                    binding.tvPlaylistDurationInMin.text =
                        String.format("%s %s", duration, duration.getWordMinuteInCorrectView())
                    val tracksCountText =
                        "${trackList?.count()} ${trackList?.count()?.getWordTrackInCorrectView()}"
                    binding.tvPlaylistTracksCount.text = tracksCountText
                    binding.linelarItemPlaylist.tracksCount.text = tracksCountText
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.playlistDeleted.collect {
                    if (it) {
                        findNavController().navigateUp()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.currentPlayList.collect { playlist ->
                    with(binding) {
                        Glide.with(root)
                            .load(playlist?.coverPath)
                            .placeholder(R.drawable.empty_playlist_cover)
                            .transform(CenterCrop())
                            .into(imageCover)

                        tvPlaylistTitle.text = playlist?.playlistTitle

                        if (playlist?.playListDescription?.isNotEmpty() == true) {
                            tvPlaylistDescription.text = playlist.playListDescription
                            tvPlaylistDescription.isVisible = true
                        } else {
                            tvPlaylistDescription.text = ""
                        }

                        rvTrackList.adapter = trackAdapter

                        with(linelarItemPlaylist) {
                            Glide.with(requireContext())
                                .load(playlist?.coverPath)
                                .placeholder(R.drawable.ic_placeholder)
                                .transform(CenterCrop(), RoundedCorners(2))
                                .into(playlistCover)
                            playlistTitle.text = playlist?.playlistTitle
                            val tracksCountText =
                                "${playlist?.tracksCount} ${playlist?.tracksCount?.getWordTrackInCorrectView()}"
                            tracksCount.text = tracksCountText
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.showToast.collect {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsPlaylistViewModel.isTrackListIsEmpty.collect { value ->
                    if ((value != null) and (value == true)) {
                        Toast.makeText(
                            requireContext(),
                            "В плейлисте нет добавленных треков!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val PLAYLIST_ID = "PLAYLIST_ID"

        fun createArgs(playlistId: Int) = bundleOf(PLAYLIST_ID to playlistId)
    }
}