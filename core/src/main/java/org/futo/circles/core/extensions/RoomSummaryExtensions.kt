package org.futo.circles.core.extensions

import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun RoomSummary.getCircleAvatarUrl() =
    getTimelineRoomFor(roomId)?.roomSummary()?.avatarUrl?.takeIf { it.isNotEmpty() }
        ?: avatarUrl