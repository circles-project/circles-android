package org.futo.circles.feature.photos

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import org.futo.circles.core.picker.PickGalleryListener
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : RoomsFragment() {

    override val viewModel by viewModel<PhotosViewModel>()

    private var pickGalleryListener: PickGalleryListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickGalleryListener = parentFragment as? PickGalleryListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fbAddRoom.setIsVisible(pickGalleryListener == null)
    }

    override fun onRoomListItemClicked(room: RoomListItem) {
        pickGalleryListener?.onGalleryChosen(room.id) ?: run {
            findNavController().navigate(PhotosFragmentDirections.toGalleryFragment(room.id))
        }
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(PhotosFragmentDirections.toCreateGalleryDialogFragment())
    }
}