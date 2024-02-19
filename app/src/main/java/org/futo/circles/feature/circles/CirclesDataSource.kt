package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelinesLiveData
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleInvitesNotificationListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class CirclesDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    fun getCirclesFlow() = combine(
        getTimelinesLiveData().asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { timelines, _ ->
        withContext(Dispatchers.IO) { buildCirclesList(timelines) }
    }.distinctUntilChanged()

    private fun buildCirclesList(timelines: List<RoomSummary>): List<CircleListItem> {
        val invitesCount = timelines.filter { it.membership == Membership.INVITE }.size

        val joinedCircles =
            getJoinedCirclesIds().mapNotNull { getJoinedRoomById(it)?.roomSummary() }

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

    fun getJoinedCirclesIds(): List<String> {
        val circlesSpaceId = spacesTreeAccountDataSource.getRoomIdByKey(
            CIRCLES_SPACE_ACCOUNT_DATA_KEY
        ) ?: return emptyList()
        val ids = getJoinedRoomById(circlesSpaceId)?.roomSummary()?.spaceChildren
            ?.map { it.childRoomId }
            ?.filter { getJoinedRoomById(it) != null }
        return ids ?: emptyList()
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