package com.example.playlistmaker.rv

import android.content.SharedPreferences
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.search_classes.SearchHistory

class TrackAdapterSearchHistory(private var preferences: SharedPreferences): TrackAdapter(){

    private val maxLength = 10

    init {
        val temp = SearchHistory.getTracks(preferences)
        if (temp != null) trackList.addAll(temp)
    }

    fun addSongToList(song: Song){
        val index = trackList.indexOf(song)
        if (index >= 0) {
            trackList.remove(song)
            notifyItemRemoved(index)
        } else if (trackList.size == maxLength) {
            trackList.removeAt(trackList.size - 1)
            notifyItemRemoved(trackList.size - 1)
        }

        trackList.add(0, song)
        notifyItemInserted(0)
        notifyItemRangeChanged(0, trackList.size)

        SearchHistory.saveTrack(trackList, preferences)
    }

    fun clearAll(){
        val size = trackList.size
        trackList.clear()
        notifyItemRangeRemoved(0, size)
        SearchHistory.saveTrack(trackList, preferences)
    }
}