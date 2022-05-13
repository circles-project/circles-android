package com.futo.circles.mapping

import com.futo.circles.extensions.getRoomOwners
import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.model.FollowingListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toFollowingListItem(circleId: String) = FollowingListItem(
    id = roomId,
    name = nameOrId(),
    ownerName = getRoomOwners(roomId).firstOrNull()?.displayName ?: "",
    avatarUrl = avatarUrl,
    updatedTime = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
    isMyTimeline = getTimelineRoomFor(circleId)?.roomId == roomId
)