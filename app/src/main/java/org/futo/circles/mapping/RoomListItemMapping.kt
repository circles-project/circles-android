package org.futo.circles.mapping

import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo
import org.matrix.android.sdk.api.session.room.peeking.PeekResult

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.toRoomInfo() = RoomInfo(
    title = nameOrId(),
    avatarUrl = avatarUrl
)

fun RoomSummary.toJoinedGroupListItem() = JoinedGroupListItem(
    id = roomId,
    info = toRoomInfo(),
    topic = topic,
    isEncrypted = isEncrypted,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    unreadCount = notificationCount
)

fun RoomSummary.toInviteGroupListItem() = InvitedGroupListItem(
    id = roomId,
    info = toRoomInfo(),
    isEncrypted = isEncrypted,
    inviterName = getInviterName()
)

fun RoomSummary.toJoinedCircleListItem(isShared: Boolean = false) = JoinedCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    isShared = isShared,
    followingCount = spaceChildren?.size?.takeIf { it != 0 }?.minus(1) ?: 0,
    followedByCount = getFollowersCount(),
    unreadCount = getCircleUnreadMessagesCount()
)

fun RoomSummary.toInviteCircleListItem() = InvitedCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    inviterName = getInviterName()
)

fun RoomSummary.toRequestCircleListItem() = RequestCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    requesterName = getInviterName()
)

fun RoomSummary.toGalleryListItem() = GalleryListItem(
    id = roomId,
    info = toRoomInfo()
)

fun RoomSummary.toSelectableRoomListItem(selected: Boolean = false) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)

private fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0

private fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName()
        ?: ""

private fun RoomSummary.getCircleUnreadMessagesCount(): Int {
    var unreadInCircle = 0
    spaceChildren?.forEach {
        val unreadInChildRoom =
            MatrixSessionProvider.currentSession?.getRoomSummary(it.childRoomId)?.notificationCount
                ?: 0
        unreadInCircle += unreadInChildRoom
    }
    return unreadInCircle
}

fun RoomSummary.toTimelineRoomListItem() = TimelineRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isJoined = membership == Membership.JOIN
)

fun SpaceChildInfo.toTimelineRoomListItem() = TimelineRoomListItem(
    id = childRoomId,
    info = RoomInfo(
        title = name?.takeIf { it.isNotEmpty() } ?: childRoomId,
        avatarUrl = avatarUrl ?: ""
    ),
    isJoined = false
)