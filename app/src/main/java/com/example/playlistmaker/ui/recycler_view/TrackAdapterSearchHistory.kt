package com.example.playlistmaker.ui.recycler_view

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track

class TrackAdapterSearchHistory(private val tracksInteractor: TracksInteractor): TrackAdapter(){

    private val maxLength = 10

    init {
        tracksInteractor.getTracksFromLocalStorage(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                trackList.addAll(tracks)
            }

        })
    }

    fun addSongToList(track: Track){
        val index = trackList.indexOf(track)
        if (index >= 0) {
            trackList.remove(track)
            notifyItemRemoved(index)
        } else if (trackList.size == maxLength) {
            trackList.removeAt(trackList.size - 1)
            notifyItemRemoved(trackList.size - 1)
        }

        trackList.add(0, track)
        notifyItemInserted(0)
        notifyItemRangeChanged(0, trackList.size)

        tracksInteractor.saveTracksToLocalStorage(trackList)
    }

    fun clearAll(){
        val size = trackList.size
        trackList.clear()
        notifyItemRangeRemoved(0, size)
        tracksInteractor.saveTracksToLocalStorage(trackList)
    }
}