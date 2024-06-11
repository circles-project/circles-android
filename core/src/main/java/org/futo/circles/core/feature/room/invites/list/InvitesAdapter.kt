package org.futo.circles.core.feature.room.invites.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.RoomInviteListItem

enum class InviteViewType { Circle, Group, Photo }

class InvitesAdapter(
    private val onInviteClicked: (RoomInviteListItem, Boolean) -> Unit,
    private val onUnblurProfileIconClicked: (RoomInviteListItem) -> Unit
) : BaseRvAdapter<RoomInviteListItem, InviteViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position).roomType) {
        CircleRoomTypeArg.Circle -> InviteViewType.Circle.ordinal
        CircleRoomTypeArg.Group -> InviteViewType.Group.ordinal
        CircleRoomTypeArg.Photo -> InviteViewType.Photo.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (InviteViewType.entries[viewType]) {
        InviteViewType.Circle -> InvitedCircleViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            },
            onShowProfileIconClicked = { position ->
                onUnblurProfileIconClicked(getItem(position))
            })

        InviteViewType.Group -> InvitedGroupViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            },
            onShowProfileIconClicked = { position ->
                onUnblurProfileIconClicked(getItem(position))
            })

        InviteViewType.Photo ->
            InvitedGalleryViewHolder(
                parent = parent,
                onInviteClicked = { position, isAccepted ->
                    onInviteClicked(getItem(position), isAccepted)
                },
                onShowProfileIconClicked = { position ->
                    onUnblurProfileIconClicked(getItem(position))
                })
    }


    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}