package com.example.playlistmaker.ui.main.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.media_library.activity.MediaLibraryActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            buttonSearch.setOnClickListener {
                goToActivity(SearchActivity::class.java)
            }
            buttonMediaLibrary.setOnClickListener {
                goToActivity(MediaLibraryActivity::class.java)
            }
            buttonSettings.setOnClickListener {
                goToActivity(SettingsActivity::class.java)
            }
        }
    }

    private fun goToActivity(cls: Class<out Activity>){
        val intent = Intent(this, cls)
        startActivity(intent)
    }
}