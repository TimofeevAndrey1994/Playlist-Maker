package com.example.playlistmaker.ui.search.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_player.fragments.MediaPlayerFragment
import com.example.playlistmaker.ui.search.recycler_view.TrackAdapter
import com.example.playlistmaker.ui.search.screen_state.ScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragmentBinding<FragmentSearchBinding>() {

    private val searchViewModel: SearchViewModel by viewModel()

    private var isClickAllowed = true

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val adapterTrack = TrackAdapter()
    private val adapterTrackSearch = TrackAdapter()

    private var searchText = ""

    private var textWatcher: TextWatcher? = null


    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    searchViewModel.searchWithDebounce(searchText)
                }
            }

            clearIcon.setOnClickListener {
                searchEditText.setText("")
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

        searchViewModel.observeTracksState().observe(viewLifecycleOwner) { state ->
            renderScreenState(state)
        }
    }

    override fun onDestroyView() {
        textWatcher?.let {
            binding.searchEditText.removeTextChangedListener(it)
        }
        super.onDestroyView()
    }

    private fun openPlayer(track: Track, updateView: Boolean = false) {
        searchViewModel.saveTrackToLocalStorage(track, updateView)
        findNavController().navigate(
            R.id.action_searchFragment_to_mediaPlayerFragment,
            MediaPlayerFragment.createArgs(track.trackId)
        )
    }

    private fun itemClickWithDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_IN_MLS)
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

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_IN_MLS = 1000L
    }
}