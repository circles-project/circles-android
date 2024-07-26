package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelinesLiveData
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleInvitesNotificationListItem
import org.futo.circles.model.CircleListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class CirclesDataSource @Inject constructor() {

    fun getCirclesFlow() = combine(
        getTimelinesLiveData().asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { timelines, _ ->
        withContext(Dispatchers.IO) { buildCirclesList(timelines) }
    }.distinctUntilChanged()

    private fun buildCirclesList(timelines: List<RoomSummary>): List<CircleListItem> {
        val joinedTimelines = timelines
            .filter { it.membership == Membership.JOIN }
            .map { it.toJoinedCircleListItem() }

        val invitesCount = timelines.filter { it.membership == Membership.INVITE }.size
        var knocksCount = 0
        joinedTimelines.forEach { knocksCount += it.knockRequestsCount }

        val displayList = mutableListOf<CircleListItem>().apply {
            if (invitesCount > 0 || knocksCount > 0) {
                add(CircleInvitesNotificationListItem(invitesCount, knocksCount))
            }
            addAll(joinedTimelines)
        }
        return displayList
    }
}