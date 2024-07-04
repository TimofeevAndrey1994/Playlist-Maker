package com.example.playlistmaker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        buttonSearch.setOnClickListener{
            goToActivity(SearchActivity::class.java)
        }

        val buttonMediaLibrary = findViewById<Button>(R.id.button_media_library)
        buttonMediaLibrary.setOnClickListener {
            goToActivity(MediaLibraryActivity::class.java)
        }

        val buttonSettings = findViewById<Button>(R.id.button_settings)
        buttonSettings.setOnClickListener{
            goToActivity(SettingsActivity::class.java)
        }
    }

    private fun goToActivity(cls: Class<out Activity>){
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}