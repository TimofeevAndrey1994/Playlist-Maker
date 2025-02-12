package com.example.playlistmaker.ui.media_player.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.LinelarPlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist

class LinelarPlaylistAdapter: RecyclerView.Adapter<LinelarPlaylistViewHoler> () {
    private var onItemClickListener: OnItemClickListener? = null
    private val playlists: ArrayList<Playlist> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinelarPlaylistViewHoler {
        val binding = LinelarPlaylistItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return LinelarPlaylistViewHoler(binding)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: LinelarPlaylistViewHoler, position: Int) {
        if (onItemClickListener!=null) {
            holder.binding.root.setOnClickListener {
                onItemClickListener!!.onItemClick(playlists[position])
            }
        }
        holder.bind(playlists[position])
    }

    fun addAll(playlists: List<Playlist>){
        this.playlists.clear()
        this.playlists.addAll(playlists)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }
}

fun interface OnItemClickListener {
    fun onItemClick(playlist: Playlist)
}