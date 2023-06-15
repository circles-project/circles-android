package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.CIRCLE_TAG
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.core.utils.isCircleShared
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.RequestCircleListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class CirclesDataSource @Inject constructor() {

    fun getCirclesFlow() = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })?.asFlow()
        ?.flowOn(Dispatchers.IO)
        ?.distinctUntilChanged()
        ?.mapLatest { list -> withContext(Dispatchers.IO) { buildCirclesList(list) } }

    private fun buildCirclesList(list: List<RoomSummary>): List<CircleListItem> {
        val invites =
            list.filter { isInviteToCircleTimeline(it) }.map { it.toInviteCircleListItem() }
        val joinedCircles = list.filter { isJoinedCircle(it) }
        val sharedCircles =
            joinedCircles.filter { joinedCircle -> isCircleShared(joinedCircle.roomId) }
        val privateCircles = joinedCircles - sharedCircles.toSet()
        val requests = getKnockRequestToSharedTimelines(sharedCircles)

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

    private fun getKnockRequestToSharedTimelines(sharedCircles: List<RoomSummary>): List<RequestCircleListItem> {
        val requests = mutableListOf<RequestCircleListItem>()

        sharedCircles.forEach {
            val sharedTimeline = MatrixSessionProvider.currentSession?.getRoom(
                it.spaceChildren?.firstOrNull()?.childRoomId ?: ""
            ) ?: return@forEach
            val sharedTimelineSummary = sharedTimeline.roomSummary() ?: return@forEach

            val knockingMembers =
                sharedTimeline.membershipService().getRoomMembers(roomMemberQueryParams {
                    memberships = listOf(Membership.KNOCK)
                })

            if (knockingMembers.isEmpty()) return@forEach
            knockingMembers.forEach { user ->
                requests.add(
                    RequestCircleListItem(
                        id = sharedTimeline.roomId,
                        info = sharedTimelineSummary.toRoomInfo(),
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