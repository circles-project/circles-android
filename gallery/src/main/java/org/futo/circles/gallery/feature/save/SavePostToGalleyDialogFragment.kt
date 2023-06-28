package org.futo.circles.gallery.feature.save

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.room.select.SelectRoomsListener
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentSavePostToGalleryBinding
import org.futo.circles.gallery.feature.select.SelectGalleriesFragment

@AndroidEntryPoint
class SavePostToGalleyDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentSavePostToGalleryBinding::inflate),
    HasLoadingState, SelectRoomsListener {

    override val fragment: Fragment = this
    private val args: SavePostToGalleyDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<SavePostToGalleryViewModel>()

    //    {
//        parametersOf(args.roomId, args.eventId)
//    }
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
                showSuccess(getString(R.string.saved))
                onBackPressed()
            }
        )
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnSave.isEnabled = rooms.isNotEmpty()
    }
}