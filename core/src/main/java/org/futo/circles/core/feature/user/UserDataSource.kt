package org.futo.circles.core.feature.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.model.TimelineHeaderItem
import org.futo.circles.core.model.TimelineListItem
import org.futo.circles.core.model.TimelineRoomListItem
import org.futo.circles.core.model.toTimelineRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

@ViewModelScoped
class UserDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    private val userId: String = savedStateHandle.getOrThrow("userId")

    private val session by lazy { MatrixSessionProvider.getSessionOrThrow() }

    val userLiveData = session.userService().getUserLive(userId).map {
        it.getOrNull() ?: session.getUserOrDefault(userId)
    }

    suspend fun getTimelinesFlow() = combine(
        getAllFollowingTimelinesFlow(),
        getAllSharedTimelinesFlow()
    ) { followingTimelines, sharedTimelines ->
        buildList(followingTimelines, sharedTimelines)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun buildList(
        followingTimelines: List<TimelineRoomListItem>,
        sharedTimelines: List<TimelineRoomListItem>
    ): List<TimelineListItem> = mutableListOf<TimelineListItem>().apply {
        val allItems = mutableListOf<TimelineRoomListItem>().apply {
            addAll(followingTimelines)
            addAll(sharedTimelines)
        }.distinctBy { it.id }

        val following = allItems.filter { it.isJoined }
        if (following.isNotEmpty()) {
            add(TimelineHeaderItem.followingHeader)
            addAll(following)
        }
        val others = allItems - following.toSet()
        if (others.isNotEmpty()) {
            add(TimelineHeaderItem.othersHeader)
            addAll(others)
        }
    }

    private fun getAllFollowingTimelinesFlow() = session.roomService()
        .getRoomSummariesLive(roomSummaryQueryParams())
        .map { list -> filterUsersTimelines(list) }.asFlow()

    private suspend fun getAllSharedTimelinesFlow() = session.roomService().getRoomSummaryLive(
        sharedCircleDataSource.getSharedCircleFor(userId)?.roomId ?: ""
    ).asFlow().map { sharedSummary ->
        sharedSummary.getOrNull()?.let { mapSharedTimelines(it) } ?: emptyList()
    }

    private suspend fun mapSharedTimelines(sharedSummary: RoomSummary): List<TimelineRoomListItem> =
        session.spaceService().querySpaceChildren(sharedSummary.roomId).children.map {
            it.toTimelineRoomListItem()
        }

    private fun filterUsersTimelines(list: List<RoomSummary>): List<TimelineRoomListItem> {
        return list.mapNotNull { summary ->
            if (isUsersCircleTimeline(summary)) summary.toTimelineRoomListItem()
            else null
        }
    }

    private fun isUsersCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.JOIN &&
                getRoomOwner(summary.roomId)?.userId == userId

}