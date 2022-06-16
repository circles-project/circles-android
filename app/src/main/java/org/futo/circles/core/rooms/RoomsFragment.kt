package org.futo.circles.core.rooms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.list.RoomsDividerDecoration
import org.futo.circles.core.rooms.list.GalleryViewHolder
import org.futo.circles.core.rooms.list.RoomListItemViewType
import org.futo.circles.core.rooms.list.RoomsListAdapter
import org.futo.circles.databinding.RoomsFragmentBinding
import org.futo.circles.extensions.bindToFab
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.model.RoomListItem

interface HasInvites {
    fun onAcceptInviteClicked(room: RoomListItem)
}

abstract class RoomsFragment : Fragment(R.layout.rooms_fragment) {

    abstract val viewModel: RoomsViewModel
    protected val binding by viewBinding(RoomsFragmentBinding::bind)
    private val listAdapter by lazy {
        RoomsListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            }
        )
    }

    abstract fun onRoomListItemClicked(room: RoomListItem)
    abstract fun navigateToCreateRoom()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            addItemDecoration(RoomsDividerDecoration(context, RoomListItemViewType.Gallery.ordinal))
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData?.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onInviteClicked(room: RoomListItem, isAccepted: Boolean) {
        if (this !is HasInvites) return

        if (isAccepted) onAcceptInviteClicked(room)
        else viewModel.rejectInvite(room.id)
    }
}