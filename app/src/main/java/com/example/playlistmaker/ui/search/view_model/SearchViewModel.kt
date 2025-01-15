package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.search.screen_state.ScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val tracksList = ArrayList<Track>()
    private val searchHistoryTracksList = tracks()

    private fun tracks() = ArrayList<Track>()

    private var searchJob: Job? = null

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

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY_IN_MLS)
            search()
        }
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
        viewModelScope.launch {
            tracksInteractor.searchTracks(searchText)
                .collect { pair ->
                    val tracks = pair.first
                    val message = pair.second
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
        }
    }

    private fun setScreenState(state: ScreenState) {
        searchTracksState.postValue(state)
    }

    private fun fillArrayFromLocalStorage() {
        viewModelScope.launch {
            tracksInteractor.getTracksFromLocalStorage()
                .collect{ tracks ->
                    searchHistoryTracksList.clear()
                    searchHistoryTracksList.addAll(tracks)
                }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_IN_MLS = 2000L
    }
}