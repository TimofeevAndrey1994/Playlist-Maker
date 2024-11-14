package com.example.playlistmaker.ui.search.view_model

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.search.screen_state.ScreenState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SearchViewModel : ViewModel(), KoinComponent {

    private val tracksInteractor: TracksInteractor by inject()

    private val tracksList = ArrayList<Track>()
    private val searchHistoryTracksList = ArrayList<Track>()

    private val mainThreadHandler = Handler(Looper.getMainLooper())

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
        if (searchText.isEmpty()) {
            return
        }
        this.searchText = searchText
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_IN_MLS)
    }

    fun clearTracksSearchHistory() {
        tracksInteractor.clearLocalStorage()
        searchHistoryTracksList.clear()
        setScreenState(ScreenState.StateWithData(tracksList))
    }

    private fun search() {
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