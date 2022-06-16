package org.futo.circles.feature.circles

import org.futo.circles.core.rooms.data_source.RoomsDataSource
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.RoomListItem
import org.futo.circles.model.TIMELINE_TYPE
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