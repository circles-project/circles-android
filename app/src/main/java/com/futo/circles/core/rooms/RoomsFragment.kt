package com.futo.circles.core.rooms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.list.RoomsDividerDecoration
import com.futo.circles.core.rooms.list.RoomListItemViewType
import com.futo.circles.core.rooms.list.RoomsListAdapter
import com.futo.circles.databinding.RoomsFragmentBinding
import com.futo.circles.extensions.bindToFab
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.model.RoomListItem

interface HasInvites {
    fun onAcceptInviteClicked(room: RoomListItem)
}

abstract class RoomsFragment : Fragment(R.layout.rooms_fragment) {

    abstract val viewModel: RoomsViewModel
    private val binding by viewBinding(RoomsFragmentBinding::bind)
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