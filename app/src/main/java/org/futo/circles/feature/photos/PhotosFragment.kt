package org.futo.circles.feature.photos

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.list.RoomsDividerDecoration
import org.futo.circles.core.picker.PickGalleryListener
import org.futo.circles.core.rooms.RoomsFragment
import org.futo.circles.core.rooms.list.RoomListItemViewType
import org.futo.circles.core.rooms.list.RoomsListAdapter
import org.futo.circles.databinding.FragmentRoomsBinding
import org.futo.circles.extensions.bindToFab
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.RoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : Fragment(R.layout.fragment_rooms) {

    private val viewModel by viewModel<PhotosViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)

    private var pickGalleryListener: PickGalleryListener? = null
    private val listAdapter by lazy {
        RoomsListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            }
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickGalleryListener = parentFragment as? PickGalleryListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.fbAddRoom.setIsVisible(pickGalleryListener == null)
        binding.rvRooms.apply {
            addItemDecoration(RoomsDividerDecoration(context, RoomListItemViewType.Gallery.ordinal))
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData?.observeData(this) { listAdapter.submitList(it) }
    }

    private fun onRoomListItemClicked(room: RoomListItem) {
        pickGalleryListener?.onGalleryChosen(room.id) ?: run {
            findNavController().navigate(PhotosFragmentDirections.toGalleryFragment(room.id))
        }
    }
    private fun navigateToCreateRoom() {
        findNavController().navigate(PhotosFragmentDirections.toCreateGalleryDialogFragment())
    }
}