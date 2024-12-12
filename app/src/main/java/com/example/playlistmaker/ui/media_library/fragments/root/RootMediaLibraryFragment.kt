package com.example.playlistmaker.ui.media_library.fragments.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_library.view_pager2.MediaLibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class RootMediaLibraryFragment: BaseFragmentBinding<FragmentMediaLibraryBinding> () {

    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMediaLibraryBinding {
        return FragmentMediaLibraryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager2.adapter = MediaLibraryViewPagerAdapter(childFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tablayout, binding.viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                else -> tab.text = getString(R.string.playlists)
            }
        }

        tabLayoutMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

}