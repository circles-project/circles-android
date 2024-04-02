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
import org.futo.circles.core.mapping.toCircleUserSummary
import org.futo.circles.core.model.MutualFriendListItem
import org.futo.circles.core.model.TimelineHeaderItem
import org.futo.circles.core.model.TimelineListItem
import org.futo.circles.core.model.TimelineRoomListItem
import org.futo.circles.core.model.toTimelineRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelinesLiveData
import org.futo.circles.core.utils.spaceType
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User
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
        getAllTimelinesFlow(),
        getAllSharedTimelinesFlow(),
        session.userService().getIgnoredUsersLive().asFlow()
    ) { allTimelines, sharedTimelines, ignoredUsers ->
        buildList(allTimelines, sharedTimelines, ignoredUsers)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun buildList(
        allTimelines: List<TimelineRoomListItem>,
        sharedTimelines: List<TimelineRoomListItem>,
        ignoredUsers: List<User>
    ): List<TimelineListItem> = mutableListOf<TimelineListItem>().apply {

        val timelinesOwnedByUser = mutableListOf<TimelineRoomListItem>().apply {
            addAll(allTimelines.filter { isUsersCircleTimeline(it.id) })
            addAll(sharedTimelines)
        }.distinctBy { it.id }

        val following = timelinesOwnedByUser.filter { it.isJoined }
        if (following.isNotEmpty()) {
            add(TimelineHeaderItem.followingHeader)
            addAll(following)
        }
        val others = timelinesOwnedByUser - following.toSet()
        if (others.isNotEmpty()) {
            add(TimelineHeaderItem.othersHeader)
            addAll(others)
        }
        val mutualFriends = getMutualFriends(allTimelines, ignoredUsers)
        if (mutualFriends.isNotEmpty()) {
            add(TimelineHeaderItem.mutualFriends)
            addAll(mutualFriends)
        }
    }

    private fun getAllTimelinesFlow() = getTimelinesLiveData(listOf(Membership.JOIN))
        .map { list -> list.map { it.toTimelineRoomListItem() } }.asFlow()

    private suspend fun getAllSharedTimelinesFlow() = session.roomService().getRoomSummaryLive(
        sharedCircleDataSource.getSharedCircleFor(userId)?.roomId ?: ""
    ).asFlow().map { sharedSummary ->
        sharedSummary.getOrNull()?.let { mapSharedTimelines(it) } ?: emptyList()
    }

    private suspend fun mapSharedTimelines(sharedSummary: RoomSummary): List<TimelineRoomListItem> =
        session.spaceService().querySpaceChildren(sharedSummary.roomId).children.mapNotNull {
            if (it.roomType == spaceType) null
            else it.toTimelineRoomListItem()
        }


    private fun getMutualFriends(
        following: List<TimelineRoomListItem>,
        ignoredUsers: List<User>
    ): List<MutualFriendListItem> {
        val ignoredUsersIds = ignoredUsers.map { it.userId }
        val mutualFriendsSet = mutableSetOf<RoomMemberSummary>()
        following.forEach {
            val members = session.getRoom(it.id)?.membershipService()?.getRoomMembers(
                roomMemberQueryParams { memberships = listOf(Membership.JOIN) }
            ) ?: emptyList()
            mutualFriendsSet.addAll(members)
        }
        return mutualFriendsSet.mapNotNull {
            if (it.userId != userId && it.userId != session.myUserId)
                MutualFriendListItem(it.toCircleUserSummary(), ignoredUsersIds.contains(it.userId))
            else null
        }
    }

    private fun isUsersCircleTimeline(roomId: String) = getRoomOwner(roomId)?.userId == userId

}