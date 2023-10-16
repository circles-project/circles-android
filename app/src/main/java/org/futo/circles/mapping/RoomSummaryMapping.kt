package org.futo.circles.mapping

import org.futo.circles.core.mapping.getInviterName
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.model.InvitedCircleListItem
import org.futo.circles.model.InvitedGroupListItem
import org.futo.circles.model.JoinedCircleListItem
import org.futo.circles.model.JoinedGroupListItem
import org.futo.circles.model.TimelineRoomListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.SpaceChildInfo


fun RoomSummary.toJoinedGroupListItem() = JoinedGroupListItem(
    id = roomId,
    info = toRoomInfo(),
    topic = topic,
    isEncrypted = isEncrypted,
    membersCount = joinedMembersCount ?: 0,
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    unreadCount = notificationCount,
    knockRequestsCount = getKnocksCount(roomId)
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
    unreadCount = getCircleUnreadMessagesCount(),
    knockRequestsCount = getKnocksCount(getTimelineRoomFor(roomId)?.roomId ?: "")
)

fun RoomSummary.toInviteCircleListItem() = InvitedCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    inviterName = getInviterName()
)

private fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0


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

fun getKnocksCount(roomId: String) =
    MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
        ?.getRoomMembers(
            roomMemberQueryParams {
                excludeSelf = true
                memberships = listOf(Membership.KNOCK)
            }
        )?.size ?: 0

fun SpaceChildInfo.toTimelineRoomListItem() = TimelineRoomListItem(
    id = childRoomId,
    info = RoomInfo(
        title = name?.takeIf { it.isNotEmpty() } ?: childRoomId,
        avatarUrl = avatarUrl ?: ""
    ),
    isJoined = false
)