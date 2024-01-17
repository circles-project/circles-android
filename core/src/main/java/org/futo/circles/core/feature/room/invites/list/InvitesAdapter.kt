package org.futo.circles.core.feature.room.invites.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.ConnectionInviteListItem
import org.futo.circles.core.model.FollowRequestListItem
import org.futo.circles.core.model.InviteHeader
import org.futo.circles.core.model.InviteListItem
import org.futo.circles.core.model.RoomInviteListItem

enum class InviteViewType { Circle, Group, Photo, Header, PeopleInvite, PeopleKnock }

class InvitesAdapter(
    private val onInviteClicked: (InviteListItem, Boolean) -> Unit,
    private val onUnblurProfileIconClicked: (InviteListItem) -> Unit
) : BaseRvAdapter<InviteListItem, InviteViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is ConnectionInviteListItem -> InviteViewType.PeopleInvite.ordinal
        is FollowRequestListItem -> InviteViewType.PeopleKnock.ordinal
        is InviteHeader -> InviteViewType.Header.ordinal
        is RoomInviteListItem -> when (item.roomType) {
            CircleRoomTypeArg.Circle -> InviteViewType.Circle.ordinal
            CircleRoomTypeArg.Group -> InviteViewType.Group.ordinal
            CircleRoomTypeArg.Photo -> InviteViewType.Photo.ordinal
        }
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

        InviteViewType.Header -> InviteHeaderViewHolder(parent)
        InviteViewType.PeopleInvite -> ConnectionInviteViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            })

        InviteViewType.PeopleKnock -> FollowRequestViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            })
    }


    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}