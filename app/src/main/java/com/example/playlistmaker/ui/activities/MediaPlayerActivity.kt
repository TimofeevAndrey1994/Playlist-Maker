package com.example.playlistmaker.ui.activities

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.utils.tryToLong
import com.example.playlistmaker.ui.activities.SearchActivity.Companion.TRACK_MODEL
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerActivity : AppCompatActivity() {

    private var mediaPlayerState: MediaPlayerState = MediaPlayerState.STATE_DEFAULT
        set(value) {
            setState(value)
            field = value
        }

    private var mediaPlayer = MediaPlayer()

    private lateinit var binding: ActivityMediaPlayerBinding

    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val updateTimeRunnable: Runnable = object : Runnable {
        @SuppressLint("DefaultLocale")
        override fun run() {
            val seconds = mediaPlayer.currentPosition / 1000L
            binding.tvSongDuration.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
            mainThreadHandler.postDelayed(this, 500)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //--- При инициализации сеттер не отрабатывает,
        //--- поэтому приводим состояние экрана в состояние DEFAUTL
        mediaPlayerState = MediaPlayerState.STATE_DEFAULT

        val track = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_MODEL, Track::class.java)
        } else {
            IntentCompat.getParcelableExtra(intent, TRACK_MODEL, Track::class.java)
        }

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
                tvYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
            tvGenreValue.text = track?.primaryGenreName
            tvCountryValue.text = track?.country

            arrowBackFromMediaPlayer.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            ivPlay.setOnClickListener {
                if (mediaPlayerState != MediaPlayerState.STATE_DEFAULT) {
                    mediaPlayerState = mediaPlayerState.getNextState()
                }
            }
        }

        if (track?.previewUrl != null) {
            initMediaPlayer(track.previewUrl)
        }
    }

    private fun stopUpdateTime() {
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
    }

    private fun startUpdateTime() {
        mainThreadHandler.post(updateTimeRunnable)
    }

    private fun resetPlayer() {
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
        mediaPlayerState = MediaPlayerState.STATE_PREPARED
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler.removeCallbacks(updateTimeRunnable)
    }

    private fun initMediaPlayer(src: String) {
        mediaPlayer.setDataSource(src)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayerState = MediaPlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            resetPlayer()
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
                    mediaPlayer.start()
                    startUpdateTime()
                }
                MediaPlayerState.STATE_PAUSED -> {
                    ivPlay.setImageResource(R.drawable.ic_play)
                    mediaPlayer.pause()
                    stopUpdateTime()
                }
            }
        }
    }

    private enum class MediaPlayerState {
        STATE_DEFAULT,
        STATE_PREPARED,
        STATE_PLAYING,
        STATE_PAUSED;

        fun getNextState(): MediaPlayerState = when (this) {
            STATE_DEFAULT -> STATE_PREPARED
            STATE_PREPARED -> STATE_PLAYING
            STATE_PLAYING -> STATE_PAUSED
            STATE_PAUSED -> STATE_PLAYING
        }
    }
}