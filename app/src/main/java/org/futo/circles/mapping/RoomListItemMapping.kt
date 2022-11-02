package org.futo.circles.mapping

import org.futo.circles.extensions.getTimelineRoomFor
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.getUserOrDefault
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

fun RoomSummary.toGalleryListItem() = GalleryListItem(
    id = roomId,
    info = toRoomInfo()
)

fun RoomSummary.toSelectableRoomListItem(selected: Boolean = false) = SelectableRoomListItem(
    id = roomId,
    info = toRoomInfo(),
    isSelected = selected
)

fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0

fun RoomSummary.getInviterName() =
    MatrixSessionProvider.currentSession?.getUserOrDefault(inviterId ?: "")?.notEmptyDisplayName() ?: ""