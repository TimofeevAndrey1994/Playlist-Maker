package com.example.playlistmaker.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.model.Song

open class TrackAdapter: RecyclerView.Adapter<TrackViewHolder> () {
    private var onItemClickListener: OnItemClickListener? = null
    protected val trackList: ArrayList<Song> = ArrayList<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return TrackViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        if (onItemClickListener!=null) {
            holder.binding.root.setOnClickListener {
                onItemClickListener!!.onItemClick(trackList[position])
            }
        }
        holder.bind(trackList[position])
    }

    fun addAll(songList: ArrayList<Song>){
        trackList.addAll(songList)
        notifyDataSetChanged()
    }

    fun clear(isRefreshList: Boolean = false){
        trackList.clear()
        if (isRefreshList) notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

}

fun interface OnItemClickListener {
    fun onItemClick(song: Song)
}