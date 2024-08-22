package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.model.Song
import com.example.playlistmaker.retrofit.ItunesAPI
import com.example.playlistmaker.retrofit.ItunesResponse
import com.example.playlistmaker.rv.TrackAdapter
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

    private var songs: ArrayList<Song> = ArrayList()
    private val adapter = TrackAdapter(songs)

    private var searchText = ""
    private var currentState: ScreenState = ScreenState.StateWithData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with (binding){
            recyclerView.adapter = adapter
            arrowBackFromSearch.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            searchEditText.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search(searchText)
                }
                false
            }
            clearIcon.setOnClickListener {
                songs.clear()
                adapter.notifyDataSetChanged()
                searchEditText.setText("")
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
            }

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    clearIcon.isVisible = !s.isNullOrEmpty()
                    searchText = searchEditText.text.toString()
                }
                override fun afterTextChanged(s: Editable?) {}
            }

            searchEditText.addTextChangedListener(textWatcher)

            btnRefresh.setOnClickListener {
                search(searchText)
            }
        }
        setScreenState(currentState)
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
        private const val BASE_URL         = "https://itunes.apple.com"
    }

    private fun search(text: String) {
        if (text.isEmpty()) return
        songs.clear()
        itunesService.getSongs(text)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(
                    call: Call<ItunesResponse>,
                    response: Response<ItunesResponse>
                ) {
                    if (response.code() == 200) {
                        val currentData = response.body()?.results!!
                        if (currentData.isNotEmpty()) {
                            songs.addAll(currentData)
                            adapter.notifyDataSetChanged()
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
                    imgError.setImageResource(state.imageRes)
                    imgError.visibility = View.VISIBLE

                    textViewErrorMessage.text = state.errorText
                    textViewErrorMessage.visibility = View.VISIBLE

                    btnRefresh.isVisible = state.isButtonRefreshVisible
                }
                is ScreenState.StateWithData -> {
                    imgError.visibility = View.GONE
                    textViewErrorMessage.visibility = View.GONE
                    btnRefresh.visibility = View.GONE
                }
            }
        }
    }

    sealed class ScreenState {
        data object StateWithData : ScreenState()
        sealed class ErrorOrEmptyState(@DrawableRes val imageRes: Int, val errorText: String, val isButtonRefreshVisible: Boolean
        ): ScreenState() {
            class ConnectionError(errorText: String): ErrorOrEmptyState(R.drawable.connection_error, errorText, true)
            class NoData(errorText: String): ErrorOrEmptyState(R.drawable.nothing_to_show, errorText, false)
        }
    }
}