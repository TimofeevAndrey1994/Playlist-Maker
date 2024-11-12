package com.example.playlistmaker.ui.media_player.activity

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media_player.player_state.MediaPlayerState
import com.example.playlistmaker.ui.media_player.view_model.MediaPlayerViewModel
import com.example.playlistmaker.ui.search.activity.SearchActivity.Companion.TRACK_MODEL
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaPlayerBinding
    private lateinit var mediaPlayerViewModel: MediaPlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trackId = intent.getLongExtra(TRACK_MODEL, -1)

        mediaPlayerViewModel = ViewModelProvider(
            this,
            MediaPlayerViewModel.getViewModelFactory(trackId)
        )[MediaPlayerViewModel::class.java]

        mediaPlayerViewModel.observeCurrentTrack().observe(this) { track ->
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
                if (releaseDate != null)
                    tvYearValue.text =
                        SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
                tvGenreValue.text = track?.primaryGenreName
                tvCountryValue.text = track?.country

            }
        }

        binding.ivPlay.setOnClickListener {
            mediaPlayerViewModel.nextState()
        }

        binding.arrowBackFromMediaPlayer.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        mediaPlayerViewModel.observeMediaPlayerState().observe(this) { state ->
            setState(state)
        }
        mediaPlayerViewModel.observeCurrentTrackTime().observe(this){ currentTrackTime ->
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

}