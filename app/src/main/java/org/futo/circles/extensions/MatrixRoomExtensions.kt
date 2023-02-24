package org.futo.circles.extensions

import org.futo.circles.core.SYSTEM_NOTICES_TAG
import org.futo.circles.model.PRIVATE_CIRCLES_SPACE_TAG
import org.futo.circles.model.SHARED_CIRCLES_SPACE_TAG
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

fun Room.getTimelineRoom(): Room? {
    val session = MatrixSessionProvider.currentSession ?: return null
    val childId = roomSummary()?.spaceChildren?.firstOrNull {
        val room = session.getRoom(it.childRoomId)?.roomSummary()
        room?.inviterId == null && room?.roomType == TIMELINE_TYPE
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

fun getSharedCirclesSpaceId(): String? = getSpaceIdByTag(SHARED_CIRCLES_SPACE_TAG)
fun getPrivateCirclesSpaceId(): String? = getSpaceIdByTag(PRIVATE_CIRCLES_SPACE_TAG)

fun getSpaceIdByTag(tag: String): String? {
    val session = MatrixSessionProvider.currentSession ?: return null
    return session.roomService().getRoomSummaries(roomSummaryQueryParams { excludeType = null })
        .firstOrNull { it.hasTag(tag) && it.roomType == RoomType.SPACE }?.roomId
}

fun Room.getReadByCountForEvent(eventId: String): Int {
    val members = membershipService().getRoomMembers(roomMemberQueryParams {
        memberships = listOf(Membership.JOIN)
    })
    var counter = 0
    members.forEach {
        val readReceiptEventId = readService().isEventRead(eventId, it.userId)
        if (readReceiptEventId) counter++
    }
    return if (counter > 0) counter - 1 else 0
}