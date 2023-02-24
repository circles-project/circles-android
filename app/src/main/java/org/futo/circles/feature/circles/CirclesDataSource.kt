package org.futo.circles.feature.circles

import androidx.lifecycle.map
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CirclesDataSource {

    fun getCirclesLiveData() = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
        ?.map { list -> filterCircles(list) }

    private fun filterCircles(list: List<RoomSummary>): List<CircleListItem> {
        return list.mapNotNull { summary ->
            if (isCircle(summary)) summary.toJoinedCircleListItem()
            else if (isInviteToCircleTimeline(summary)) summary.toInviteCircleListItem()
            else null
        }.sortedBy { it.membership }
    }

    private fun isCircle(summary: RoomSummary) =
        summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

    suspend fun rejectInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(roomId)
    }
}