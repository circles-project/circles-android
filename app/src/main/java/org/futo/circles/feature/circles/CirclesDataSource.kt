package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleInvitesNotificationListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
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
        val invitesCount = list.filter { isInviteToCircleTimeline(it) }.size

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

        val displayList = mutableListOf<CircleListItem>().apply {
            if (invitesCount > 0)
                add(CircleInvitesNotificationListItem(invitesCount))

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
        val ids = getJoinedRoomById(circlesSpaceId)?.roomSummary()?.spaceChildren
            ?.map { it.childRoomId }
            ?.filter { getJoinedRoomById(it) != null }
        return ids ?: emptyList()
    }

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

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