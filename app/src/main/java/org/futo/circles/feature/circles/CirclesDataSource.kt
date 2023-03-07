package org.futo.circles.feature.circles

import androidx.lifecycle.map
import org.futo.circles.core.utils.isCircleShared
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.mapping.toRequestCircleListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CirclesDataSource {

    fun getCirclesLiveData() = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
        ?.map { list -> buildCirclesList(list) }

    private fun buildCirclesList(list: List<RoomSummary>): List<CircleListItem> {
        val invites =
            list.filter { isInviteToCircleTimeline(it) }.map { it.toInviteCircleListItem() }
        val requests =
            list.filter { isRequestToCircleTimeline(it) }.map { it.toRequestCircleListItem() }
        val joinedCircles = list.filter { isJoinedCircle(it) }
        val sharedCircles =
            joinedCircles.filter { joinedCircle -> isCircleShared(joinedCircle.roomId) }
        val privateCircles = joinedCircles - sharedCircles.toSet()

        val displayList = mutableListOf<CircleListItem>().apply {
            addSection(CirclesHeaderItem.requestsCirclesHeader, requests)
            addSection(CirclesHeaderItem.invitesCirclesHeader, invites)
            addSection(
                CirclesHeaderItem.sharedCirclesHeader,
                sharedCircles.map { it.toJoinedCircleListItem(true) })
            addSection(
                CirclesHeaderItem.privateCirclesHeader,
                privateCircles.map { it.toJoinedCircleListItem(false) })
        }
        return displayList
    }

    private fun isJoinedCircle(summary: RoomSummary) =
        summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

    private fun isRequestToCircleTimeline(summary: RoomSummary) =
         summary.membership == Membership.KNOCK

    suspend fun rejectInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(roomId)
    }

    private fun MutableList<CircleListItem>.addSection(
        title: CirclesHeaderItem,
        items: List<CircleListItem>
    ) {
        if (items.isNotEmpty()) {
            add(title)
            addAll(items)
        }
    }
}