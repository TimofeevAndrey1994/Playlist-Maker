package com.example.playlistmaker

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        buttonSearch.setOnClickListener{
            showToast("Нажата кнопка поиск")
        }

        val buttonMediaLibrary = findViewById<Button>(R.id.button_media_library)
        val onClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                showToast("Нажата кнопка медиатека")
            }
        }
        buttonMediaLibrary.setOnClickListener(onClickListener)

        val buttonSettings = findViewById<Button>(R.id.button_settings)
        buttonSettings.setOnClickListener{
            showToast("Нажата кнопка настройки")
        }
    }

    private fun showToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}