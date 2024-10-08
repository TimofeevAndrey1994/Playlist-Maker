package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.retrofit.ItunesAPI
import com.example.playlistmaker.retrofit.ItunesResponse
import com.example.playlistmaker.retrofit.RetrofitInstance
import com.example.playlistmaker.rv.TrackAdapter
import com.example.playlistmaker.rv.TrackAdapterSearchHistory
import com.example.playlistmaker.utils.SEARCH_HISTORY
import com.example.playlistmaker.utils.SONG_MODEL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true

    private val searchRunnable = Runnable { search() }

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private lateinit var binding: ActivitySearchBinding

    private val adapterTrack = TrackAdapter()
    private val adapterTrackSearch by lazy {
        TrackAdapterSearchHistory(preferences)
    }
    private val preferences by lazy {
        getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)
    }

    private var searchText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            setScreenState(ScreenState.StateWithData)

            adapterTrack.setOnItemClickListener{ song ->
                if(itemClickWithDebounce()) {
                    openPlayer(song)
                }
            }
            recyclerView.adapter = adapterTrack

            adapterTrackSearch.setOnItemClickListener{ song ->
                if(itemClickWithDebounce()) {
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
                }
                else {
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
                    }
                    else {
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

    private fun searchWithDebounce(){
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
    }

    private fun openPlayer(song: Song){
        adapterTrackSearch.addSongToList(song)

        val intent = Intent(this@SearchActivity, MediaPlayerActivity::class.java)
        intent.putExtra(SONG_MODEL, song)
        startActivity(intent)
    }

    private fun search() {
        if (searchText.isEmpty()) return
        adapterTrack.clear()
        setScreenState(ScreenState.StateWithProgressBar)
        RetrofitInstance.itunesService.getSongs(searchText)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>
                ) {
                    if (response.code() == 200) {
                        val currentData = response.body()?.results!!
                        if (currentData.isNotEmpty()) {
                            adapterTrack.addAll(currentData)
                            setScreenState(ScreenState.StateWithData)
                        } else {
                            setScreenState(ScreenState.ErrorOrEmptyState.NoData(getString(R.string.no_data)))
                        }
                    } else {
                        setScreenState(ScreenState.ErrorOrEmptyState.ConnectionError(getString(R.string.error_connection)))
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    setScreenState(ScreenState.ErrorOrEmptyState.ConnectionError(getString(R.string.error_connection)))
                }

            })
    }

    private fun itemClickWithDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setScreenState(state: ScreenState) {

        fun setViewsVisible(visibilityFlag: Boolean, vararg views: View){
            views.forEach { it.isVisible = visibilityFlag }
        }

        with(binding) {
            when (state) {
                is ScreenState.ErrorOrEmptyState -> {
                    setViewsVisible(false, recyclerView, searchHistory.root, progressBar)
                    setViewsVisible(true, searchErrorState.root, searchErrorState.imgError, searchErrorState.textViewErrorMessage)
                    searchErrorState.imgError.setImageResource(state.imageRes)
                    searchErrorState.textViewErrorMessage.text       = state.errorText
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