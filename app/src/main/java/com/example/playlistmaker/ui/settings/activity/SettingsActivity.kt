package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory()
        )[SettingsViewModel::class.java]

        with(binding) {
            arrowBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            textViewShare.setOnClickListener {
                settingsViewModel.shareApp()
            }

            textViewTextToSupport.setOnClickListener {
                settingsViewModel.textToSupport()
            }

            textViewUserAgreement.setOnClickListener {
                settingsViewModel.openTerms()
            }

            settingsViewModel.observeCurrentDarkTheme().observe(this@SettingsActivity) { value ->
                themeSwitcher.isChecked = value
            }

            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.switchTheme(isChecked)
            }
        }
    }
}