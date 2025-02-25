package com.example.playlistmaker.ui.playlist_details.fragment

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.bundleOf
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistEditBinding
import com.example.playlistmaker.ui.base.BaseFragmentBinding
import com.example.playlistmaker.ui.playlist_details.view_model.EditPlaylistViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : BaseFragmentBinding<FragmentPlaylistEditBinding>() {

    private val editPlaylistViewModel: EditPlaylistViewModel by viewModel{
        var id = 0
        arguments?.let {
            id = it.getInt(PLAYLIST_ID)
        }
        parametersOf(id)
    }

    private var textWatcher: TextWatcher? = null

    private var selectedUri: Uri? = null

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            selectedUri = uri
            Glide.with(requireContext())
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(16))
                .into(binding.imageCover)
        }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistEditBinding {
        return FragmentPlaylistEditBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnCreatePlaylist.isEnabled = ((s != null) and (s != ""))
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                editPlaylistViewModel.showToast.collect { value ->
                    Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
                }
            }
        }

        with(binding) {
            editPlaylistViewModel.currentPlaylist().observe(viewLifecycleOwner) { playlist ->
                with(binding){
                    Glide.with(root)
                        .load(playlist.coverPath)
                        .placeholder(R.drawable.ic_placeholder)
                        .transform(CenterCrop(), RoundedCorners(16))
                        .into(imageCover)

                    selectedUri = playlist.coverPath?.toUri()

                    etPlaylistName.setText(playlist.playlistTitle)
                    etPlaylistAbout.setText(playlist.playListDescription)
                }
            }

            btnCreatePlaylist.isEnabled = (etPlaylistName.text.isNotEmpty())

            etPlaylistName.addTextChangedListener(textWatcher)

            btnCreatePlaylist.setOnClickListener {
                editPlaylistViewModel.createOrUpdatePlaylist(
                    binding.etPlaylistName.text.toString(),
                    etPlaylistAbout.text.toString(),
                    selectedUri
                )
                findNavController().navigateUp()
            }

            imageCover.setOnClickListener {
                activityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            arrowBackCreatePlaylist.setOnClickListener {
                if ((selectedUri != null) or (etPlaylistName.text.isNotEmpty()) or (etPlaylistAbout.text.isNotEmpty())) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(requireContext().getString(R.string.wanna_to_finish_creating))
                        .setMessage(requireContext().getString(R.string.all_unsaved_data_will_be_lose))
                        .setNeutralButton(requireContext().getString(R.string.cancel)) { _, _ ->

                        }
                        .setPositiveButton(requireContext().getString(R.string.done)) { _, _ ->
                            findNavController().navigateUp()
                        }
                        .show()
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher = null
    }

    companion object {
        private const val PLAYLIST_ID = "PLAYLIST_ID"

        fun createArgs(playlistId: Int) = bundleOf(PLAYLIST_ID to playlistId)
    }
}