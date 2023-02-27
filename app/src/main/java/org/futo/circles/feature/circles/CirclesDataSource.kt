package org.futo.circles.feature.circles

import androidx.lifecycle.map
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.getSharedCirclesSpaceId
import org.futo.circles.extensions.getTimelineRoomFor
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoomSummary
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
        val joinedCircles = list.filter { isJoinedCircle(it) }
        val sharedCircles = joinedCircles.filter { joinedCircle ->
            val timelineId = getTimelineRoomFor(joinedCircle.roomId)?.roomId
            getSharedCirclesIds().contains(timelineId)
        }
        val privateCircles = joinedCircles - sharedCircles.toSet()

        val displayList = mutableListOf<CircleListItem>()
        if (invites.isNotEmpty()) {
            displayList.add(CirclesHeaderItem.invitesCirclesHeader)
            displayList.addAll(invites)
        }
        if (sharedCircles.isNotEmpty()) {
            displayList.add(CirclesHeaderItem.sharedCirclesHeader)
            displayList.addAll(sharedCircles.map { it.toJoinedCircleListItem(true) })
        }
        if (privateCircles.isNotEmpty()) {
            displayList.add(CirclesHeaderItem.privateCirclesHeader)
            displayList.addAll(privateCircles.map { it.toJoinedCircleListItem(false) })
        }
        return displayList
    }

    private fun getSharedCirclesIds() = getSharedCirclesSpaceId()?.let {
        MatrixSessionProvider.currentSession?.getRoomSummary(it)?.spaceChildren?.map { it.childRoomId }
    } ?: emptyList()

    private fun isJoinedCircle(summary: RoomSummary) =
        summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

    suspend fun rejectInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(roomId)
    }
}