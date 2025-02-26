package com.example.playlistmaker.ui.media_library.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.playlistmaker.domain.model.Playlist


class PlaylistAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {
    private val playListList = ArrayList<Playlist>()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding =
            PlaylistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return playListList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        onItemClickListener.let {
            holder.binding.root.setOnClickListener {
                onItemClickListener?.onItemClick(playListList[position])
            }
        }
        holder.bind(playListList[position])
    }

    fun addAll(list: List<Playlist>) {
        playListList.clear()
        playListList.addAll(list)
        notifyDataSetChanged()
    }

    fun setOnClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun interface OnItemClickListener {
        fun onItemClick(playlist: Playlist)
    }
}