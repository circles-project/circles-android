package org.futo.circles.core.extensions

import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.RoomInfo
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.toRoomInfo(isCircle: Boolean): RoomInfo =
    if (isCircle) {
        val timeline = getTimelineRoomFor(roomId)?.roomSummary()
        RoomInfo(timeline?.nameOrId() ?: nameOrId(),
            timeline?.avatarUrl?.takeIf { it.isNotEmpty() } ?: avatarUrl
        )
    } else RoomInfo(nameOrId(), getCircleAvatarUrl())

fun RoomSummary.getCircleAvatarUrl() =
    getTimelineRoomFor(roomId)?.roomSummary()?.avatarUrl?.takeIf { it.isNotEmpty() }
        ?: avatarUrl