package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.search.screen_state.ScreenState

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val mainThreadHandler: Handler
) : ViewModel() {

    private val tracksList = ArrayList<Track>()
    private val searchHistoryTracksList = ArrayList<Track>()

    private val searchRunnable = Runnable { search() }

    private val searchTracksState =
        MutableLiveData<ScreenState>(ScreenState.StateWithData(tracksList))

    fun observeTracksState(): LiveData<ScreenState> = searchTracksState

    private var searchText: String = ""
    private var lastSearchText: String? = null

    fun saveTrackToLocalStorage(track: Track, updateView: Boolean = false) {
        tracksInteractor.saveTrackToLocalStorage(track)
        if (updateView) {
            fillArrayFromLocalStorage()
            setScreenState(ScreenState.SearchHistoryState(searchHistoryTracksList))
        }
    }

    fun searchWithDebounce(searchText: String) {
        this.searchText = searchText

        if (searchText == lastSearchText) {
            return
        }

        tracksList.clear()
        setScreenState(ScreenState.StateWithData(tracksList))

        fillArrayFromLocalStorage()
        if ((searchText.isEmpty()) and searchHistoryTracksList.isNotEmpty()) {
            setScreenState(ScreenState.SearchHistoryState(searchHistoryTracksList))
            return
        }

        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_IN_MLS)
    }

    fun clearTracksSearchHistory() {
        tracksInteractor.clearLocalStorage()
        searchHistoryTracksList.clear()
        setScreenState(ScreenState.StateWithData(tracksList))
    }

    private fun search() {
        if (searchText.isEmpty()) {
            return
        }
        setScreenState(ScreenState.StateWithProgressBar)
        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?, message: String?) {
                if (tracks != null) {
                    tracksList.clear()
                    tracksList.addAll(tracks)
                    setScreenState(
                        if (tracks.isNotEmpty()) ScreenState.StateWithData(tracksList) else ScreenState.ErrorOrEmptyState.NoData(
                            message ?: ""
                        )
                    )
                    lastSearchText = searchText
                } else if (message != null) {
                    setScreenState(ScreenState.ErrorOrEmptyState.ConnectionError(message))
                }
            }
        })
    }

    private fun setScreenState(state: ScreenState) {
        searchTracksState.postValue(state)
    }

    private fun fillArrayFromLocalStorage() {
        tracksInteractor.getTracksFromLocalStorage(object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>?, message: String?) {
                searchHistoryTracksList.clear()
                searchHistoryTracksList.addAll(tracks ?: emptyList())
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MLS = 2000L
    }
}