package org.futo.circles.mapping

import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.model.FollowingListItem
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toFollowingListItem(circleId: String, followInCirclesCount: Int) =
    FollowingListItem(
        id = roomId,
        name = nameOrId(),
        ownerName = getRoomOwners(roomId).firstOrNull()?.displayName ?: "",
        avatarUrl = avatarUrl,
        updatedTime = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
        isMyTimeline = getTimelineRoomFor(circleId)?.roomId == roomId,
        followInCirclesCount = followInCirclesCount
    )