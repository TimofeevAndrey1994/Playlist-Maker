package com.example.playlistmaker.ui.recycler_view

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track

class TrackAdapterSearchHistory(private val tracksInteractor: TracksInteractor): TrackAdapter(){

    init {
        tracksInteractor.getTracksFromLocalStorage(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?) {
                trackList.addAll(tracks ?: emptyList())
            }

        })
    }

    fun addTrackToList(track: Track){
        trackList.clear()
        trackList.addAll(tracksInteractor.saveTrackToLocalStorage(track))
        notifyDataSetChanged()
    }

    fun clearAll(){
        tracksInteractor.clearLocalStorage()
        val size = trackList.size
        trackList.clear()
        notifyItemRangeRemoved(0, size)

    }
}