package org.futo.circles.feature.photos.save

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentSaveToGalleryBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showSuccess
import org.futo.circles.feature.photos.save.list.SelectGalleryAdapter
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SaveToGalleyDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentSaveToGalleryBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: SaveToGalleyDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<SaveToGalleryViewModel> {
        parametersOf(args.roomId, args.eventId)
    }
    private val binding by lazy {
        getBinding() as DialogFragmentSaveToGalleryBinding
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
                showSuccess(getString(R.string.saved), true)
                activity?.onBackPressed()
            }
        )
    }

    private fun onGallerySelected(gallery: SelectableRoomListItem) {
        viewModel.toggleGallerySelect(gallery)
    }
}