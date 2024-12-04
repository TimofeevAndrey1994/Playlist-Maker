package com.example.playlistmaker.ui.media_library.view_pager2

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.ui.media_library.fragments.favourite_tracks.FavouriveTracksFragment
import com.example.playlistmaker.ui.media_library.fragments.playlists.PlaylistsFragment

class MediaLibraryViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavouriveTracksFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}