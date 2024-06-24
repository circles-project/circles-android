package org.futo.circles.core.feature.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.mapping.toCircleUserSummary
import org.futo.circles.core.model.MutualFriendListItem
import org.futo.circles.core.model.TimelineHeaderItem
import org.futo.circles.core.model.TimelineListItem
import org.futo.circles.core.model.TimelineRoomListItem
import org.futo.circles.core.model.toTimelineRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelinesLiveData
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

@ViewModelScoped
class UserDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val userId: String = savedStateHandle.getOrThrow("userId")

    private val session by lazy { MatrixSessionProvider.getSessionOrThrow() }

    val userLiveData = session.userService().getUserLive(userId).map {
        it.getOrNull() ?: session.getUserOrDefault(userId)
    }

    fun getTimelinesFlow() = combine(
        getAllJoinedTimelinesFlow(),
        session.userService().getIgnoredUsersLive().asFlow()
    ) { allTimelines, ignoredUsers ->
        buildList(allTimelines, ignoredUsers)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun buildList(
        allTimelines: List<TimelineRoomListItem>,
        ignoredUsers: List<User>
    ): List<TimelineListItem> = mutableListOf<TimelineListItem>().apply {

        val timelinesOwnedByUser = mutableListOf<TimelineRoomListItem>().apply {
            addAll(allTimelines.filter { isUsersCircleTimeline(it.id) })
        }.distinctBy { it.id }

        if (timelinesOwnedByUser.isNotEmpty()) {
            add(TimelineHeaderItem.followingHeader)
            addAll(timelinesOwnedByUser)
        }
        val mutualFriends = getMutualFriends(allTimelines, ignoredUsers)
        if (mutualFriends.isNotEmpty()) {
            add(TimelineHeaderItem.mutualFriends)
            addAll(mutualFriends)
        }
    }

    private fun getAllJoinedTimelinesFlow() = getTimelinesLiveData(listOf(Membership.JOIN))
        .map { list -> list.map { it.toTimelineRoomListItem() } }.asFlow()


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