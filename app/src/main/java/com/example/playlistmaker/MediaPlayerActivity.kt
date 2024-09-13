package com.example.playlistmaker

import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var binding: ActivityMediaPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val song = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(SONG_MODEL, Song::class.java)
        } else {
            intent.getParcelableExtra(SONG_MODEL)
        }

        with(binding){
            trackName.text      = song?.trackName
            trackPerformer.text = song?.artistName
            Glide.with(root)
                .load(song?.artworkUrl100?.replaceAfterLast("/", "512x512bb.jpg"))
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(8))
                .into(trackCover)

            val trackDuration = song?.trackTime?.tryToLong()
            if (trackDuration != null){
                if (trackDuration > -1) {
                    tvDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)
                }
            }

            val album = song?.collectionName ?: ""
            groupAlbum.isVisible = album.isNotEmpty()
            tvAlbumValue.text    = song?.collectionName
            val releaseDate = song?.releaseDate
            if (releaseDate != null)
                tvYearValue.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(releaseDate)
            tvGenreValue.text    = song?.primaryGenreName
            tvCountryValue.text  = song?.country

            arrowBackFromMediaPlayer.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}