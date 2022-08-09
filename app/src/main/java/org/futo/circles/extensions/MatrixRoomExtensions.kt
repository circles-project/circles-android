package org.futo.circles.extensions

import org.futo.circles.core.SYSTEM_NOTICES_TAG
import org.futo.circles.model.TIMELINE_TAG
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

fun Room.getTimelineRoom(): Room? {
    val session = MatrixSessionProvider.currentSession ?: return null
    val childId = roomSummary()?.spaceChildren?.firstOrNull {
        val room = session.getRoom(it.childRoomId)?.roomSummary()
        room?.hasTag(TIMELINE_TAG) == true && room.inviterId == null && room.roomType == TIMELINE_TYPE
    }?.childRoomId
    return childId?.let { session.getRoom(it) }
}

fun getTimelineRoomFor(circleId: String): Room? {
    val session = MatrixSessionProvider.currentSession ?: return null
    return session.getRoom(circleId)?.getTimelineRoom()
}

fun getSystemNoticesRoomId(): String? {
    val session = MatrixSessionProvider.currentSession ?: return null
    return session.roomService().getRoomSummaries(roomSummaryQueryParams())
        .firstOrNull { it.hasTag(SYSTEM_NOTICES_TAG) }?.roomId
}