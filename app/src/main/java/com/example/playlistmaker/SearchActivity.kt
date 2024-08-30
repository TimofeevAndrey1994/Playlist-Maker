package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.retrofit.ItunesAPI
import com.example.playlistmaker.retrofit.ItunesResponse
import com.example.playlistmaker.rv.TrackAdapter
import com.example.playlistmaker.rv.TrackAdapterSearchHistory
import com.example.playlistmaker.utils.SEARCH_HISTORY
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private val adapter = TrackAdapter()
    private val adapterSearch by lazy {
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

            adapter.setOnItemClickListener{ song ->
                adapterSearch.addSongToList(song)
            }
            recyclerView.adapter = adapter
            searchHistory.rvSavedList.adapter = adapterSearch
            searchHistory.btnClearHistory.setOnClickListener {
                adapterSearch.clearAll()
                setScreenState(ScreenState.StateWithData)
            }

            arrowBackFromSearch.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search(searchText)
                }
                false
            }
            searchEditText.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus and searchText.isEmpty() and (adapterSearch.itemCount > 0)) {
                    setScreenState(ScreenState.SearchHistoryState)
                }
                else {
                    setScreenState(ScreenState.StateWithData)
                }
            }

            clearIcon.setOnClickListener {
                adapter.clear(true)
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
                    if (searchEditText.hasFocus() and searchText.isEmpty() and (adapterSearch.itemCount > 0)) {
                        setScreenState(ScreenState.SearchHistoryState)
                    }
                    else {
                        setScreenState(ScreenState.StateWithData)
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            searchEditText.addTextChangedListener(textWatcher)

            searchErrorState.btnRefresh.setOnClickListener {
                search(searchText)
            }


        }
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
        private const val BASE_URL = "https://itunes.apple.com"
    }

    private fun search(text: String) {
        if (text.isEmpty()) return
        adapter.clear()
        setScreenState(ScreenState.StateWithData)
        itunesService.getSongs(text)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>
                ) {
                    if (response.code() == 200) {
                        val currentData = response.body()?.results!!
                        if (currentData.isNotEmpty()) {
                            adapter.addAll(currentData)
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

    private fun setScreenState(state: ScreenState) {
        with(binding) {
            when (state) {
                is ScreenState.ErrorOrEmptyState -> {
                    recyclerView.visibility          = View.GONE

                    searchHistory.root.visibility    = View.GONE
                    searchErrorState.root.visibility = View.VISIBLE

                    searchErrorState.imgError.setImageResource(state.imageRes)
                    searchErrorState.imgError.visibility = View.VISIBLE

                    searchErrorState.textViewErrorMessage.text       = state.errorText
                    searchErrorState.textViewErrorMessage.visibility = View.VISIBLE

                    searchErrorState.btnRefresh.isVisible = state.isButtonRefreshVisible
                }

                is ScreenState.StateWithData -> {
                    recyclerView.visibility          = View.VISIBLE
                    searchHistory.root.visibility    = View.GONE
                    searchErrorState.root.visibility = View.GONE
                }

                ScreenState.SearchHistoryState -> {
                    recyclerView.visibility          = View.GONE
                    searchHistory.root.visibility    = View.VISIBLE
                    searchErrorState.root.visibility = View.GONE
                }
            }
        }
    }

    sealed class ScreenState {
        data object StateWithData : ScreenState()
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