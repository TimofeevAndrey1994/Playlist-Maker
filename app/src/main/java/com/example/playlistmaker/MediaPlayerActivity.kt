package com.example.playlistmaker

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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityMediaPlayerBinding
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.utils.SONG_MODEL
import com.example.playlistmaker.utils.tryToLong
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

    @SuppressLint("DefaultLocale")
    private val updateTimeRunnable: Runnable = object : Runnable {
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

        val song = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(SONG_MODEL, Song::class.java)
        } else {
            IntentCompat.getParcelableExtra(intent, SONG_MODEL, Song::class.java)
        }

        with(binding) {
            trackName.text = song?.trackName
            trackPerformer.text = song?.artistName
            Glide.with(root)
                .load(song?.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"))
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(8))
                .into(trackCover)

            val trackDuration = song?.trackTime?.tryToLong()
            if (trackDuration != null) {
                if (trackDuration > -1) {
                    tvDurationValue.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)
                }
            }

            val album = song?.collectionName ?: ""
            groupAlbum.isVisible = album.isNotEmpty()
            tvAlbumValue.text = song?.collectionName
            val releaseDate = song?.releaseDate
            if (releaseDate != null)
                tvYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
            tvGenreValue.text = song?.primaryGenreName
            tvCountryValue.text = song?.country

            arrowBackFromMediaPlayer.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            ivPlay.setOnClickListener {
                if (mediaPlayerState != MediaPlayerState.STATE_DEFAULT) {
                    mediaPlayerState = mediaPlayerState.getNextState()
                }
            }
        }

        if (song?.previewUrl != null) {
            initMediaPlayer(song.previewUrl)
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
            STATE_DEFAULT -> MediaPlayerState.STATE_PREPARED
            STATE_PREPARED -> MediaPlayerState.STATE_PLAYING
            STATE_PLAYING -> MediaPlayerState.STATE_PAUSED
            STATE_PAUSED -> MediaPlayerState.STATE_PLAYING
        }
    }
}