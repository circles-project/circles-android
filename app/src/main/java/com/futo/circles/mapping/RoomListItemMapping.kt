package com.futo.circles.mapping

import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.model.*
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary

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
    timestamp = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis()
)

fun RoomSummary.toInviteGroupListItem() = InvitedGroupListItem(
    id = roomId,
    info = toRoomInfo(),
    isEncrypted = isEncrypted,
    inviterName = getInviterName()
)

fun RoomSummary.toJoinedCircleListItem() = JoinedCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    followingCount = spaceChildren?.size?.takeIf { it != 0 }?.minus(1) ?: 0,
    followedByCount = getFollowersCount(),
)

fun RoomSummary.toInviteCircleListItem() = InvitedCircleListItem(
    id = roomId,
    info = toRoomInfo(),
    inviterName = getInviterName()
)

fun RoomSummary.toSelectableRoomListItem() = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = false
)

fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0

fun RoomSummary.getInviterName(): String =
    MatrixSessionProvider.currentSession?.getUser(inviterId ?: "")?.displayName ?: ""