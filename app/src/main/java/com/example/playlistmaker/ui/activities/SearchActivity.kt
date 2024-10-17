package com.example.playlistmaker.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.ui.recycler_view.TrackAdapter
import com.example.playlistmaker.ui.recycler_view.TrackAdapterSearchHistory

class SearchActivity : AppCompatActivity() {

    private val tracksInteractor by lazy {
        Creator.provideTracksInteractor(this)
    }
    private var isClickAllowed = true

    private val searchRunnable = Runnable { search() }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivitySearchBinding

    private val adapterTrack = TrackAdapter()
    private val adapterTrackSearch by lazy {
        TrackAdapterSearchHistory(tracksInteractor)
    }

    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            setScreenState(ScreenState.StateWithData)

            adapterTrack.setOnItemClickListener { song ->
                if (itemClickWithDebounce()) {
                    openPlayer(song)
                }
            }
            recyclerView.adapter = adapterTrack

            adapterTrackSearch.setOnItemClickListener { song ->
                if (itemClickWithDebounce()) {
                    openPlayer(song)
                }
            }
            searchHistory.rvSavedList.adapter = adapterTrackSearch
            searchHistory.btnClearHistory.setOnClickListener {
                adapterTrackSearch.clearAll()
                setScreenState(ScreenState.StateWithData)
            }

            arrowBackFromSearch.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus and searchText.isEmpty() and (adapterTrackSearch.itemCount > 0)) {
                    setScreenState(ScreenState.SearchHistoryState)
                } else {
                    setScreenState(ScreenState.StateWithData)
                }
            }

            clearIcon.setOnClickListener {
                adapterTrack.clear(true)
                searchEditText.setText("")
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearIcon.isVisible = !s.isNullOrEmpty()
                    searchText = searchEditText.text.toString()
                    if (searchEditText.hasFocus() and searchText.isEmpty() and (adapterTrackSearch.itemCount > 0)) {
                        setScreenState(ScreenState.SearchHistoryState)
                    } else {
                        searchWithDebounce()
                        setScreenState(ScreenState.StateWithData)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            searchEditText.addTextChangedListener(textWatcher)

            searchErrorState.btnRefresh.setOnClickListener {
                searchWithDebounce()
            }
        }
    }

    private fun searchWithDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchEditText.setText(savedInstanceState.getString(EDIT_TEXT_SEARCH, ""))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_SEARCH, searchText)
    }

    companion object {
        private const val EDIT_TEXT_SEARCH = "EDIT_TEXT_SEARCH"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_MODEL = "TRACK"
    }

    private fun openPlayer(track: Track) {
        adapterTrackSearch.addSongToList(track)

        val intent = Intent(this@SearchActivity, MediaPlayerActivity::class.java)
        intent.putExtra(TRACK_MODEL, track)
        startActivity(intent)
    }

    private fun search() {
        if (searchText.isEmpty()) return
        setScreenState(ScreenState.StateWithProgressBar)
        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(tracks: List<Track>) {
                mainThreadHandler.post {
                    adapterTrack.clear()
                    adapterTrack.addAll(tracks)
                    setScreenState(
                        if (tracks.isNotEmpty()) ScreenState.StateWithData else ScreenState.ErrorOrEmptyState.NoData(
                            getString(R.string.no_data)
                        )
                    )
                }
            }
        })
    }

    private fun itemClickWithDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setScreenState(state: ScreenState) {

        fun setViewsVisible(visibilityFlag: Boolean, vararg views: View) {
            views.forEach { it.isVisible = visibilityFlag }
        }

        with(binding) {
            when (state) {
                is ScreenState.ErrorOrEmptyState -> {
                    setViewsVisible(false, recyclerView, searchHistory.root, progressBar)
                    setViewsVisible(
                        true,
                        searchErrorState.root,
                        searchErrorState.imgError,
                        searchErrorState.textViewErrorMessage
                    )
                    searchErrorState.imgError.setImageResource(state.imageRes)
                    searchErrorState.textViewErrorMessage.text = state.errorText
                    searchErrorState.btnRefresh.isVisible = state.isButtonRefreshVisible
                }

                is ScreenState.StateWithData -> {
                    setViewsVisible(false, searchHistory.root, searchErrorState.root, progressBar)
                    setViewsVisible(true, recyclerView)
                }

                ScreenState.SearchHistoryState -> {
                    setViewsVisible(false, recyclerView, searchErrorState.root, progressBar)
                    setViewsVisible(true, searchHistory.root)
                }

                ScreenState.StateWithProgressBar -> {
                    setViewsVisible(false, searchHistory.root, searchErrorState.root, recyclerView)
                    setViewsVisible(true, progressBar)
                }
            }
        }
    }

    sealed class ScreenState {
        data object StateWithData : ScreenState()
        data object StateWithProgressBar : ScreenState()
        data object SearchHistoryState : ScreenState()
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
}