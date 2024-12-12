package com.example.playlistmaker.ui.media_library.fragments.favourite_tracks

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.media_library.view_model.FavouriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriveTracksFragment : BaseFragmentBinding<FragmentFavouriteTracksBinding>(){

    private val favouriteTracksViewModel: FavouriteTracksViewModel by viewModel()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavouriteTracksBinding {
        return FragmentFavouriteTracksBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(): FavouriveTracksFragment = FavouriveTracksFragment()
    }

}