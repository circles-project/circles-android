package com.futo.circles.feature.photos.save

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.SaveToGalleryDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.photos.save.list.SelectGalleryAdapter
import com.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SaveToGalleyDialogFragment :
    BaseFullscreenDialogFragment(SaveToGalleryDialogFragmentBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: SaveToGalleyDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<SaveToGalleryViewModel> {
        parametersOf(args.roomId, args.eventId)
    }
    private val binding by lazy {
        getBinding() as SaveToGalleryDialogFragmentBinding
    }

    private val listAdapter by lazy {
        SelectGalleryAdapter(
            onGalleryClicked = { galleryListItem -> onGallerySelected(galleryListItem) },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            btnSave.setOnClickListener {
                viewModel.saveToGallery()
                startLoading(btnSave)
            }
            binding.rvGalleries.adapter = listAdapter
        }
    }

    private fun setupObservers() {
        viewModel.galleriesLiveData.observeData(this) {
            listAdapter.submitList(it)
            binding.btnSave.isEnabled = it.firstOrNull { it.isSelected } != null
        }
        viewModel.saveResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.image_saved), true)
                activity?.onBackPressed()
            }
        )
    }

    private fun onGallerySelected(gallery: SelectableRoomListItem) {
        viewModel.toggleGallerySelect(gallery)
    }
}