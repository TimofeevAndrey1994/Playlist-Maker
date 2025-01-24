package com.example.playlistmaker.ui.media_player.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaPlayerBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.utils.MediaPlayerState
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerFragment : BaseFragmentBinding<FragmentMediaPlayerBinding>() {

    private val mediaPlayerViewModel: MediaPlayerViewModel by viewModel {
        val trackId = requireArguments().getLong(TRACK_ID, -1)
        parametersOf(trackId)
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaPlayerBinding {
        return FragmentMediaPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mediaPlayerViewModel.observeCurrentTrack().observe(viewLifecycleOwner) { track ->
            with(binding) {
                trackName.text = track?.trackName
                trackPerformer.text = track?.artistName
                Glide.with(root)
                    .load(track?.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"))
                    .placeholder(R.drawable.ic_placeholder)
                    .transform(CenterCrop(), RoundedCorners(8))
                    .into(trackCover)

                tvDurationValue.text = track?.trackTime

                val album = track?.collectionName ?: ""
                groupAlbum.isVisible = album.isNotEmpty()
                tvAlbumValue.text = track?.collectionName

                val releaseDate = track?.releaseDate
                if (releaseDate != null) {
                    tvYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
                }
                tvGenreValue.text = track?.primaryGenreName
                tvCountryValue.text = track?.country

            }
        }

        binding.ivPlay.setOnClickListener {
            mediaPlayerViewModel.nextState()
        }

        binding.arrowBackFromMediaPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivLike.setOnClickListener {
            mediaPlayerViewModel.onLike()
        }

        mediaPlayerViewModel.observeIsFavouriteTrack().observe(viewLifecycleOwner) { value ->
            binding.ivLike.setImageResource(if (value) R.drawable.ic_fill_like else R.drawable.ic_like)
        }

        mediaPlayerViewModel.observeMediaPlayerState().observe(viewLifecycleOwner) { state ->
            setState(state)
        }
        mediaPlayerViewModel.observeCurrentTrackTime()
            .observe(viewLifecycleOwner) { currentTrackTime ->
                binding.tvSongDuration.text = currentTrackTime
            }
    }

    private fun setState(state: MediaPlayerState) {
        with(binding) {
            when (state) {
                MediaPlayerState.STATE_DEFAULT, MediaPlayerState.STATE_PREPARED -> {
                    tvSongDuration.text = getString(R.string.init_song_time)
                    ivPlay.setImageResource(R.drawable.ic_play)
                }

                MediaPlayerState.STATE_PLAYING -> {
                    ivPlay.setImageResource(R.drawable.ic_pause)
                }

                MediaPlayerState.STATE_PAUSED -> {
                    ivPlay.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }

    companion object {
        private const val TRACK_ID = "TRACK_MODEL"

        fun createArgs(trackId: Long): Bundle {
            return bundleOf(TRACK_ID to trackId)
        }
    }
}