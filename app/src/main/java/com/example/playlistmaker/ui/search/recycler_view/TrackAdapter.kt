package com.example.playlistmaker.ui.search.recycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.playlistmaker.domain.model.Track

open class TrackAdapter: RecyclerView.Adapter<TrackViewHolder> () {
    private var onItemClickListener: OnItemClickListener? = null
    protected val trackList: ArrayList<Track> = ArrayList()

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

    fun addAll(trackList: List<Track>, clearPrevious: Boolean = true){
        if (clearPrevious) {
            this.trackList.clear()
        }
        this.trackList.addAll(trackList)
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
    fun onItemClick(track: Track)
}