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
import androidx.recyclerview.widget.RecyclerView
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val itunesService = retrofit.create(ItunesAPI::class.java)

    private val searchEditText by lazy {
        findViewById<EditText>(R.id.searchEditText)
    }
    private val btnRefresh by lazy {
        findViewById<Button>(R.id.btnRefresh)
    }
    private val imgError by lazy {
        findViewById<ImageView>(R.id.imgError)
    }
    private val textError by lazy {
        findViewById<TextView>(R.id.textViewErrorMessage)
    }
    private val recyclerView by lazy{
        findViewById<RecyclerView>(R.id.recyclerView)
    }

    private var songs: ArrayList<Song> = ArrayList()
    private val adapter = TrackAdapter(songs)

    private var searchText = ""
    private var currentState: ScreenState = ScreenState.StateWithData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        recyclerView.adapter = adapter

        setScreenState(currentState)

        val arrowBack = findViewById<ImageView>(R.id.arrow_back_from_search)
        arrowBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(searchText)
            }
            false
        }

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                searchText = searchEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(textWatcher)

        btnRefresh.setOnClickListener {
            search(searchText)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchEditText.setText(savedInstanceState.getString(EDIT_TEXT_SEARCH, ""))
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
        when(state){
            is ScreenState.ErrorOrEmptyState -> {
                imgError.setImageResource(state.imageRes)
                imgError.visibility = View.VISIBLE

                textError.text = state.errorText
                textError.visibility = View.VISIBLE

                btnRefresh.visibility = if (state.isButtonRefreshVisible) View.VISIBLE else View.GONE
            }
            is ScreenState.StateWithData -> {
                imgError.visibility = View.GONE
                textError.visibility = View.GONE
                btnRefresh.visibility = View.GONE
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