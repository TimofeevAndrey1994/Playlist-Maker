package com.example.playlistmaker.ui.search.recycler_view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.model.Track

class TrackViewHolder(val binding: TrackItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        with(binding) {
            Glide.with(root)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_placeholder)
                .transform(CenterCrop(), RoundedCorners(2))
                .into(trackCover)

            trackName.text = model.trackName
            trackPerformer.text = model.artistName
            trackTime.text = model.trackTime
        }
    }
}