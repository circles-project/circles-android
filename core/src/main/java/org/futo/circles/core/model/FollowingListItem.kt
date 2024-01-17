package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.room.model.RoomSummary

data class FollowingListItem(
    override val id: String,
    val name: String,
    val ownerName: String,
    val avatarUrl: String,
    val updatedTime: Long,
    val isMyTimeline: Boolean,
    val followInCirclesCount: Int
) : IdEntity<String>

fun RoomSummary.toFollowingListItem(circleId: String, followInCirclesCount: Int) =
    FollowingListItem(
        id = roomId,
        name = nameOrId(),
        ownerName = getRoomOwner(roomId)?.displayName ?: "",
        avatarUrl = avatarUrl,
        updatedTime = latestPreviewableEvent?.root?.originServerTs ?: System.currentTimeMillis(),
        isMyTimeline = getTimelineRoomFor(circleId)?.roomId == roomId,
        followInCirclesCount = followInCirclesCount
    )