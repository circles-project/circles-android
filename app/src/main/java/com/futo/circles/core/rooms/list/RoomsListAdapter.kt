package com.futo.circles.core.rooms.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.*

enum class RoomListItemViewType { JoinedGroup, JoinedCircle, InvitedGroup, InvitedCircle, Gallery }

class RoomsListAdapter(
    private val onRoomClicked: (RoomListItem) -> Unit,
    private val onInviteClicked: (RoomListItem, Boolean) -> Unit
) : BaseRvAdapter<RoomListItem, RoomViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is JoinedCircleListItem && old is JoinedCircleListItem) {
        CircleListItemPayload(
            followersCount = new.followingCount,
            followedByCount = new.followedByCount,
            needUpdateFullItem = new.info.title != old.info.title || new.info.avatarUrl != old.info.avatarUrl
        )
    } else if (new is JoinedGroupListItem && old is JoinedGroupListItem) {
        GroupListItemPayload(
            topic = new.topic.takeIf { it != old.topic },
            isEncrypted = new.isEncrypted.takeIf { it != old.isEncrypted },
            membersCount = new.membersCount.takeIf { it != old.membersCount },
            timestamp = new.timestamp.takeIf { it != old.timestamp },
            needUpdateFullItem = new.info.title != old.info.title || new.info.avatarUrl != old.info.avatarUrl
        )
    } else null
}) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGroupListItem -> RoomListItemViewType.JoinedGroup.ordinal
        is InvitedGroupListItem -> RoomListItemViewType.InvitedGroup.ordinal
        is JoinedCircleListItem -> RoomListItemViewType.JoinedCircle.ordinal
        is InvitedCircleListItem -> RoomListItemViewType.InvitedCircle.ordinal
        is GalleryListItem -> RoomListItemViewType.Gallery.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomViewHolder = when (RoomListItemViewType.values()[viewType]) {
        RoomListItemViewType.JoinedGroup -> JoinedGroupViewHolder(
            parent = parent,
            onGroupClicked = { position -> onRoomClicked(getItem(position)) }
        )
        RoomListItemViewType.InvitedGroup -> InvitedGroupViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            }
        )
        RoomListItemViewType.JoinedCircle -> JoinedCircleViewHolder(
            parent = parent,
            onCircleClicked = { position -> onRoomClicked(getItem(position)) }
        )
        RoomListItemViewType.InvitedCircle -> InvitedCircleViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            }
        )
        RoomListItemViewType.Gallery -> GalleryViewHolder(
            parent = parent,
            onGalleryClicked = { position -> onRoomClicked(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: RoomViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                if (payload is CircleListItemPayload && holder is JoinedCircleViewHolder) {
                    if (payload.needUpdateFullItem) holder.bind(getItem(position))
                    else holder.bindPayload(payload)
                } else if (payload is GroupListItemPayload && holder is JoinedGroupViewHolder) {
                    if (payload.needUpdateFullItem) holder.bind(getItem(position))
                    else holder.bindPayload(payload)
                }
            }
        }
    }
}