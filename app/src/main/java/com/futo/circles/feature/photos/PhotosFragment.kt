package com.futo.circles.feature.photos

import androidx.navigation.fragment.findNavController
import com.futo.circles.core.rooms.RoomsFragment
import com.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : RoomsFragment() {

    override val viewModel by viewModel<PhotosViewModel>()

    override fun onRoomListItemClicked(room: RoomListItem) {
        findNavController().navigate(PhotosFragmentDirections.toGalleryFragment(room.id))
    }

    override fun navigateToCreateRoom() {
        findNavController().navigate(PhotosFragmentDirections.toCreateRoomDialogFragment())
    }
}