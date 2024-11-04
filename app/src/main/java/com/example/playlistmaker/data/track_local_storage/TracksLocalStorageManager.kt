package com.example.playlistmaker.data.track_local_storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class TracksLocalStorageManager(context: Context): TracksLocalStorage {

    private val maxLength = 10

    private val searchHistoryKey = "SEARCH_HISTORY"

    private val preferences: SharedPreferences = context.getSharedPreferences(searchHistoryKey, Context.MODE_PRIVATE)

    private val gsonObject by lazy {
        Gson()
    }

    override fun getTrackFromLocalStorageById(trackId: Long): Track? {
        return getTracks().find { it.trackId == trackId }
    }

    override fun saveTrackToLocalStorage(track: Track): ArrayList<Track> {
        val trackList = getTracks()
        val index = trackList.indexOf(track)
        if (index >= 0) {
            trackList.remove(track)
        } else if (trackList.size == maxLength) {
            trackList.removeAt(trackList.size - 1)
        }

        trackList.add(0, track)

        val json = gsonObject.toJson(trackList)
        preferences.edit()
            .putString(searchHistoryKey, json)
            .apply()

        return trackList
    }

    override fun getTracks(): ArrayList<Track> {
        val json = preferences.getString(searchHistoryKey, "[]")
        return gsonObject.fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
    }

    override fun clearLocalStorage() {
        val json = gsonObject.toJson(emptyArray<Track>())
        preferences.edit()
            .putString(searchHistoryKey, json)
            .apply()
    }
}