package com.example.playlistmaker.ui.root

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RootActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    private var isClickAllowed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, dest, _ ->
            val setOfInvisibleBottomNavFragments = arrayOf(R.id.mediaPlayerFragment, R.id.editPlaylistFragment)
            binding.bottomNavigationView.isVisible = !setOfInvisibleBottomNavFragments.contains(dest.id)
        }
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    fun itemClickWithDebounce(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed) {
            lifecycleScope.launch {
                isClickAllowed = false
                delay(CLICK_DEBOUNCE_DELAY_IN_MLS)
                isClickAllowed = true
            }
        }
        return current
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY_IN_MLS = 1000L
    }
}