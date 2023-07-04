package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.CIRCLE_TAG
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.core.utils.isCircleShared
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.RequestCircleListItem
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class CirclesDataSource @Inject constructor() {

    val session by lazy {
        MatrixSessionProvider.currentSession
            ?: throw IllegalArgumentException("session is not created")
    }

    fun getCirclesFlow() = combine(
        session.roomService().getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            .asFlow(),
        session.roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { buildCirclesList(roomSummaries) }
    }.distinctUntilChanged()

    private fun buildCirclesList(list: List<RoomSummary>): List<CircleListItem> {
        val invites =
            list.filter { isInviteToCircleTimeline(it) }.map { it.toInviteCircleListItem() }
        val joinedCircles = list.filter { isJoinedCircle(it) }
        val sharedCircles =
            joinedCircles.filter { joinedCircle -> isCircleShared(joinedCircle.roomId) }
        val privateCircles = joinedCircles - sharedCircles.toSet()
        val requests = getKnockRequestToSharedTimelines(joinedCircles)

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

    private fun getKnockRequestToSharedTimelines(joinedCircles: List<RoomSummary>): List<RequestCircleListItem> {
        val requests = mutableListOf<RequestCircleListItem>()

        joinedCircles.forEach {
            val timeline = getTimelineRoomFor(it.roomId) ?: return@forEach
            val timelineRoomInfo = timeline.roomSummary()?.toRoomInfo() ?: return@forEach

            val knockingMembers =
                timeline.membershipService().getRoomMembers(roomMemberQueryParams {
                    memberships = listOf(Membership.KNOCK)
                })

            if (knockingMembers.isEmpty()) return@forEach
            knockingMembers.forEach { user ->
                requests.add(
                    RequestCircleListItem(
                        id = timeline.roomId,
                        info = timelineRoomInfo,
                        requesterName = user.displayName
                            ?: UserUtils.removeDomainSuffix(user.userId),
                        requesterId = user.userId
                    )
                )
            }
        }
        return requests
    }


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