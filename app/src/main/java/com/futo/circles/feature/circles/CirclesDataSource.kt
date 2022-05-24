package com.futo.circles.feature.circles

import com.futo.circles.core.rooms.data_source.RoomsDataSource
import com.futo.circles.mapping.toInviteCircleListItem
import com.futo.circles.mapping.toJoinedCircleListItem
import com.futo.circles.model.CIRCLE_TAG
import com.futo.circles.model.RoomListItem
import com.futo.circles.model.TIMELINE_TYPE
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class CirclesDataSource : RoomsDataSource() {

    override fun filterRooms(list: List<RoomSummary>): List<RoomListItem> {
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
}