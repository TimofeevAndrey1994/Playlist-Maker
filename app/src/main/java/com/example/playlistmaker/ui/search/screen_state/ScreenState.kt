package com.example.playlistmaker.ui.search.screen_state

import androidx.annotation.DrawableRes
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track

sealed class ScreenState {
    class StateWithData(val tracks: ArrayList<Track>) : ScreenState()
    data object StateWithProgressBar : ScreenState()
    class SearchHistoryState(val tracks: ArrayList<Track>) : ScreenState()
    sealed class ErrorOrEmptyState(
        @DrawableRes val imageRes: Int,
        val errorText: String,
        val isButtonRefreshVisible: Boolean
    ) : ScreenState() {
        class ConnectionError(errorText: String) :
            ErrorOrEmptyState(R.drawable.connection_error, errorText, true)

        class NoData(errorText: String) :
            ErrorOrEmptyState(R.drawable.nothing_to_show, errorText, false)
    }
}