package com.example.playlistmaker.ui.media_library.recycler_view

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.utils.getWordTrackInCorrectView

class PlaylistViewHolder(private val binding: PlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Playlist){
        with(binding){
            Glide.with(root)
                .load(model.coverPath)
                .placeholder(R.drawable.ic_placeholder)
                .transform(CenterCrop(), RoundedCorners(8))
                .into(ivPlaylistCover)

            tvPlaylistTitle.text = model.playlistTitle
            val tracksCountText = "${model.tracksCount} ${model.tracksCount.getWordTrackInCorrectView()}"
            tvPlaylistTracksCount.text = tracksCountText
        }
    }

}