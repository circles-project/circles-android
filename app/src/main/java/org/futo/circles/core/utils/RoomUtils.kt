package org.futo.circles.core.utils

import org.futo.circles.core.SYSTEM_NOTICES_TAG
import org.futo.circles.extensions.getRoomOwners
import org.futo.circles.extensions.getTimelineRoom
import org.futo.circles.model.SHARED_CIRCLES_SPACE_TAG
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

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

fun getSharedCircleFor(userId: String) = MatrixSessionProvider.currentSession?.roomService()
    ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })?.firstOrNull { summary ->
        summary.roomType == RoomType.SPACE && summary.membership == Membership.JOIN &&
                getRoomOwners(summary.roomId).map { it.userId }.contains(userId)
    }

fun getSpaceIdByTag(tag: String): String? {
    val session = MatrixSessionProvider.currentSession ?: return null
    return session.roomService().getRoomSummaries(roomSummaryQueryParams { excludeType = null })
        .firstOrNull { it.hasTag(tag) && it.roomType == RoomType.SPACE }?.roomId
}

fun isCircleShared(circleId: String): Boolean {
    val timelineId = getTimelineRoomFor(circleId)?.roomId
    val sharedCirclesTimelinesIds = getSharedCirclesSpaceId()?.let {
        MatrixSessionProvider.currentSession?.getRoomSummary(it)?.spaceChildren?.map { it.childRoomId }
    } ?: emptyList()
    return sharedCirclesTimelinesIds.contains(timelineId)
}