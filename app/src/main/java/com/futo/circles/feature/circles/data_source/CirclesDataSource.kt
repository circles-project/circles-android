package com.futo.circles.feature.circles.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.mapping.toInviteCircleListItem
import com.futo.circles.mapping.toJoinedCircleListItem
import com.futo.circles.model.CIRCLE_TAG
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.TIMELINE_TYPE
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class CirclesDataSource {

    val session = MatrixSessionProvider.currentSession

    fun filterCircles(list: List<RoomSummary>): List<CircleListItem> {
        return list.mapNotNull { summary ->
            if (isCircle(summary)) summary.toJoinedCircleListItem()
            else if (isInviteToCircleTimeline(summary)) summary.toInviteCircleListItem()
            else null
        }.sortedBy { it.membership }
    }

    suspend fun rejectInvite(roomId: String) = createResult {
        session?.leaveRoom(roomId)
    }

    private fun isCircle(summary: RoomSummary) =
        summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

}