package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        val onClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                goToActivity(MediaLibraryActivity::class.java)
            }
        }
        buttonMediaLibrary.setOnClickListener(onClickListener)

        val buttonSettings = findViewById<Button>(R.id.button_settings)
        buttonSettings.setOnClickListener{
            goToActivity(SettingsActivity::class.java)
        }
    }

    private fun goToActivity(cls: Class<*>){
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}