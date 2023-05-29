package org.futo.circles.feature.photos.save

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.SelectRoomsListener
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentSavePostToGalleryBinding
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.feature.photos.select.SelectGalleriesFragment
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SavePostToGalleyDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentSavePostToGalleryBinding::inflate),
    HasLoadingState, SelectRoomsListener {

    override val fragment: Fragment = this
    private val args: SavePostToGalleyDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<SavePostToGalleryViewModel> {
        parametersOf(args.roomId, args.eventId)
    }
    private val binding by lazy {
        getBinding() as DialogFragmentSavePostToGalleryBinding
    }

    private val selectedGalleriesFragment by lazy { SelectGalleriesFragment() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectGalleriesFragment()
        setupViews()
        setupObservers()
    }

    private fun addSelectGalleriesFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectedGalleriesFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        with(binding) {
            btnSave.setOnClickListener {
                viewModel.saveToGallery(selectedGalleriesFragment.getSelectedRooms())
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.saveResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.saved), true)
                onBackPressed()
            }
        )
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnSave.isEnabled = rooms.isNotEmpty()
    }
}