package com.futo.circles.mapping

import com.futo.circles.extensions.getRoomOwners
import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.FollowingListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary


fun RoomSummary.toCircleListItem() = CircleListItem(
    id = roomId,
    name = nameOrId(),
    followingCount = spaceChildren?.size?.takeIf { it != 0 }?.minus(1) ?: 0,
    followedByCount = getFollowersCount(),
    avatarUrl = avatarUrl
)

fun RoomSummary.nameOrId() = displayName.takeIf { it.isNotEmpty() } ?: roomId

fun RoomSummary.getFollowersCount(): Int =
    getTimelineRoomFor(roomId)?.roomSummary()?.otherMemberIds?.size ?: 0

fun RoomSummary.toFollowingListItem(circleId: String) = FollowingListItem(
    id = roomId,
    name = nameOrId(),
    ownerName = getRoomOwners(roomId).firstOrNull()?.displayName ?: "",
    avatarUrl = avatarUrl,
    updatedTime = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    isMyTimeline = getTimelineRoomFor(circleId)?.roomId == roomId
)