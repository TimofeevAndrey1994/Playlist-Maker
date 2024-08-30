package com.example.playlistmaker.rv

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.utils.tryToLong
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(val binding: TrackItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Song) {
        with(binding) {
            Glide.with(root)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .transform(RoundedCorners(2))
                .into(trackCover)

            trackName.text = model.trackName
            trackPerformer.text = model.artistName
            val trackDuration = model.trackTime.tryToLong()
            if (trackDuration > -1)
                trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackDuration)
            else
                trackTime.text = ""
        }
    }
}