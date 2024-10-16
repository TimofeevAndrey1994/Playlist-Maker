package com.example.playlistmaker.data.impl

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.TracksLocalStorage
import com.example.playlistmaker.domain.model.Track
import com.google.gson.Gson

class TracksSearchHistoryManager(private val context: Context): TracksLocalStorage {

    private val searchHistoryKey = "SEARCH_HISTORY"

    private val preferences: SharedPreferences = context.getSharedPreferences(searchHistoryKey, Context.MODE_PRIVATE)

    private val gsonObject by lazy {
        Gson()
    }

    override fun saveTracks(tracks: ArrayList<Track>) {
        val json = gsonObject.toJson(tracks)
        preferences.edit()
            .putString(searchHistoryKey, json)
            .apply()
    }

    override fun getTracks(): List<Track> {
        val json = preferences.getString(searchHistoryKey, "[]")
        return gsonObject.fromJson(json, Array<Track>::class.java).toCollection(ArrayList())
    }
}