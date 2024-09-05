package com.example.playlistmaker.search_classes

import android.content.SharedPreferences
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.utils.SEARCH_HISTORY
import com.google.gson.Gson

class SearchHistory() {

    companion object {
        fun saveTrack(songs: ArrayList<Song>, preferences: SharedPreferences) {
            val json = Gson().toJson(songs)
            preferences.edit()
                .putString(SEARCH_HISTORY, json)
                .apply()
        }
        fun getTracks(preferences: SharedPreferences): Array<Song>? {
            val json = preferences.getString(SEARCH_HISTORY, null)
            return Gson().fromJson(json, Array<Song>::class.java)
        }
    }

}