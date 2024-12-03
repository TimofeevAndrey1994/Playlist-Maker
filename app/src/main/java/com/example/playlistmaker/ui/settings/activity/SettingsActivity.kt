package com.example.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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