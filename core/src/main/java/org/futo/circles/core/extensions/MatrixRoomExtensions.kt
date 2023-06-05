package org.futo.circles.core.extensions

import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership

fun Room.getTimelineRoom(): Room? {
    val session = MatrixSessionProvider.currentSession ?: return null
    val childId = roomSummary()?.spaceChildren?.firstOrNull {
        val room = session.getRoom(it.childRoomId)?.roomSummary()
        room?.inviterId == null && room?.roomType == TIMELINE_TYPE
    }?.childRoomId
    return childId?.let { session.getRoom(it) }
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