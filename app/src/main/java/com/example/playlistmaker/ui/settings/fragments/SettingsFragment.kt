package com.example.playlistmaker.ui.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: BaseFragmentBinding<FragmentSettingsBinding> (){

    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            textViewShare.setOnClickListener {
                settingsViewModel.shareApp()
            }

            textViewTextToSupport.setOnClickListener {
                settingsViewModel.textToSupport()
            }

            textViewUserAgreement.setOnClickListener {
                settingsViewModel.openTerms()
            }

            settingsViewModel.observeCurrentDarkTheme().observe(viewLifecycleOwner) { value ->
                themeSwitcher.isChecked = value
            }

            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                settingsViewModel.switchTheme(isChecked)
            }
        }
    }
}