package com.example.playlistmaker.ui.search.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.media_player.activity.MediaPlayerActivity
import com.example.playlistmaker.ui.search.activity.recycler_view.TrackAdapter
import com.example.playlistmaker.ui.search.screen_state.ScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var searchViewModel: SearchViewModel

    private var isClickAllowed = true

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivitySearchBinding

    private val adapterTrack = TrackAdapter()
    private val adapterTrackSearch = TrackAdapter()

    private var searchText = ""

    private lateinit var textWatcher: TextWatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchViewModel = ViewModelProvider(this, SearchViewModel.getViewModelFactory())[SearchViewModel::class.java]

        with(binding) {

            adapterTrack.setOnItemClickListener { song ->
                if (itemClickWithDebounce()) {
                    openPlayer(song)
                }
            }
            recyclerView.adapter = adapterTrack

            adapterTrackSearch.setOnItemClickListener { song ->
                if (itemClickWithDebounce()) {
                    openPlayer(song, true)
                }
            }
            searchHistory.rvSavedList.adapter = adapterTrackSearch

            searchHistory.btnClearHistory.setOnClickListener {
                searchViewModel.clearTracksSearchHistory()
            }

            arrowBackFromSearch.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus){
                   searchViewModel.searchWithDebounce(searchText)
                }
            }

            clearIcon.setOnClickListener {
                searchEditText.setText("")
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            textWatcher = object : TextWatcher {
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

                    searchViewModel.searchWithDebounce(searchText)
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            searchEditText.addTextChangedListener(textWatcher)

            searchErrorState.btnRefresh.setOnClickListener {
                searchViewModel.searchWithDebounce(searchText)
            }
        }

        searchViewModel.observeTracksState().observe(this) { state ->
            renderScreenState(state)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.searchEditText.removeTextChangedListener(textWatcher)
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val TRACK_MODEL = "TRACK"
    }

    private fun openPlayer(track: Track, updateView: Boolean = false) {
        searchViewModel.saveTrackToLocalStorage(track, updateView)
        val intent = Intent(this@SearchActivity, MediaPlayerActivity::class.java)
        intent.putExtra(TRACK_MODEL, track.trackId)
        startActivity(intent)
    }

    private fun itemClickWithDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun renderScreenState(state: ScreenState) {

        fun setViewsVisible(visibilityFlag: Boolean, vararg views: View) {
            views.forEach { it.isVisible = visibilityFlag }
        }

        adapterTrack.clear(false)

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
                    adapterTrack.addAll(state.tracks)
                    setViewsVisible(false, searchHistory.root, searchErrorState.root, progressBar)
                    setViewsVisible(true, recyclerView)
                }

                is ScreenState.SearchHistoryState -> {
                    adapterTrackSearch.addAll(state.tracks)
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

}