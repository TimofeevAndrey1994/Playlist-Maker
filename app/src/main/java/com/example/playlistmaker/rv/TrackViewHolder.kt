package com.example.playlistmaker.rv

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackCover      = itemView.findViewById<ImageView>(R.id.trackCover)
    private val trackName       = itemView.findViewById<TextView>(R.id.trackName)
    private val trackPerformer  = itemView.findViewById<TextView>(R.id.trackPerformer)
    private val trackTime       = itemView.findViewById<TextView>(R.id.trackTime)

    fun bind(model: Track){
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder)
            .transform(RoundedCorners(2))
            .into(trackCover)

        trackName.text      = model.trackName
        trackPerformer.text = model.artistName
        trackTime.text      = model.trackTime
    }
}