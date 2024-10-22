package com.example.playlistmaker.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivityMainBinding

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