package org.futo.circles.core.feature.room.invites.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestHeaderItem
import org.futo.circles.core.model.RoomRequestListItem

enum class RoomRequestViewType { Header, Knock, CircleInvite, GroupInvite, PhotoInvite }

class RoomRequestsAdapter(
    private val onInviteClicked: (RoomInviteListItem, Boolean) -> Unit,
    private val onUnblurProfileIconClicked: (RoomInviteListItem) -> Unit,
    private val onKnockClicked: (KnockRequestListItem, Boolean) -> Unit
) : BaseRvAdapter<RoomRequestListItem, RoomRequestViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is KnockRequestListItem -> RoomRequestViewType.Knock.ordinal
        is RoomRequestHeaderItem -> RoomRequestViewType.Header.ordinal
        is RoomInviteListItem -> when (item.roomType) {
            CircleRoomTypeArg.Circle -> RoomRequestViewType.CircleInvite.ordinal
            CircleRoomTypeArg.Group -> RoomRequestViewType.GroupInvite.ordinal
            CircleRoomTypeArg.Photo -> RoomRequestViewType.PhotoInvite.ordinal
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (RoomRequestViewType.entries[viewType]) {
        RoomRequestViewType.CircleInvite -> InvitedCircleViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                (getItem(position) as? RoomInviteListItem)?.let {
                    onInviteClicked(it, isAccepted)
                }
            },
            onShowProfileIconClicked = { position ->
                (getItem(position) as? RoomInviteListItem)?.let {
                    onUnblurProfileIconClicked(it)
                }
            })

        RoomRequestViewType.GroupInvite -> InvitedGroupViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                (getItem(position) as? RoomInviteListItem)?.let {
                    onInviteClicked(it, isAccepted)
                }
            },
            onShowProfileIconClicked = { position ->
                (getItem(position) as? RoomInviteListItem)?.let {
                    onUnblurProfileIconClicked(it)
                }
            })

        RoomRequestViewType.PhotoInvite ->
            InvitedGalleryViewHolder(
                parent = parent,
                onInviteClicked = { position, isAccepted ->
                    (getItem(position) as? RoomInviteListItem)?.let {
                        onInviteClicked(it, isAccepted)
                    }
                },
                onShowProfileIconClicked = { position ->
                    (getItem(position) as? RoomInviteListItem)?.let {
                        onUnblurProfileIconClicked(it)
                    }
                })

        RoomRequestViewType.Knock -> KnockRequestViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                (getItem(position) as? KnockRequestListItem)?.let {
                    onKnockClicked(it, isAccepted)
                }
            }
        )

        RoomRequestViewType.Header -> RoomRequestHeaderViewHolder(parent = parent)
    }


    override fun onBindViewHolder(holder: RoomRequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}