package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.core.workspace.SharedCircleDataSource
import org.futo.circles.core.workspace.SpacesTreeAccountDataSource
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

class CirclesDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    fun getCirclesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            .asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { buildCirclesList(roomSummaries) }
    }.distinctUntilChanged()

    private fun buildCirclesList(list: List<RoomSummary>): List<CircleListItem> {
        val invites =
            list.filter { isInviteToCircleTimeline(it) }.map { it.toInviteCircleListItem() }

        val joinedCirclesSpaceIds = getJoinedCirclesIds()
        val joinedCircles = list.filter { isJoinedCircle(it, joinedCirclesSpaceIds) }

        val sharedCirclesTimelinesIds = sharedCircleDataSource.getSharedCirclesTimelinesIds()
        val sharedCircles = joinedCircles.filter { joinedCircle ->
            sharedCircleDataSource.isCircleShared(
                joinedCircle.roomId,
                sharedCirclesTimelinesIds
            )
        }
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

    fun isJoinedCircle(summary: RoomSummary, joinedCirclesIds: List<String>): Boolean =
        joinedCirclesIds.contains(summary.roomId)

    fun getJoinedCirclesIds(): List<String> {
        val circlesSpaceId = spacesTreeAccountDataSource.getRoomIdByKey(
            CIRCLES_SPACE_ACCOUNT_DATA_KEY
        ) ?: return emptyList()
        val sharedCircleSpaceId = sharedCircleDataSource.getSharedCirclesSpaceId()
        val ids = getJoinedRoomById(circlesSpaceId)?.roomSummary()?.spaceChildren
            ?.map { it.childRoomId }
            ?.filter { it != sharedCircleSpaceId && getJoinedRoomById(it) != null }
        return ids ?: emptyList()
    }

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