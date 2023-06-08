package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getSharedCircleFor
import org.futo.circles.core.utils.getSharedCirclesSpaceId
import org.futo.circles.core.select_users.SearchUserDataSource
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class PeopleDataSource @Inject constructor(
    private val searchUserDataSource: SearchUserDataSource
) {

    private val session = MatrixSessionProvider.currentSession
    private val profileRoomId = getSharedCirclesSpaceId() ?: ""

    suspend fun acceptFollowRequest(userId: String) = createResult {
        session?.roomService()?.getRoom(profileRoomId)?.membershipService()?.invite(userId)
    }

    suspend fun declineFollowRequest(userId: String) =
        createResult { session?.getRoom(profileRoomId)?.membershipService()?.remove(userId) }

    private fun getProfileRoomMembersKnockFlow(): Flow<List<User>> =
        session?.getRoom(profileRoomId)?.membershipService()
            ?.getRoomMembersLive(roomMemberQueryParams { memberships = listOf(Membership.KNOCK) })
            ?.map { it.map { User(it.userId, it.displayName, it.avatarUrl) } }?.asFlow() ?: flowOf()

    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow(),
        getProfileRoomMembersKnockFlow()
    ) { knowUsers, suggestions, ignoredUsers, requests ->
        buildList(knowUsers, suggestions, ignoredUsers, requests)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()

    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>,
        requests: List<User>
    ): List<PeopleListItem> {
        val uniqueItemsList = mutableListOf<PeopleListItem>().apply {
            addAll(ignoredUsers.map { it.toPeopleUserListItem(PeopleItemType.Ignored) })
            addAll(requests.map { it.toPeopleUserListItem(PeopleItemType.Request) })
            addAll(knowUsers.map { it.toPeopleUserListItem(getKnownUserItemType(it.userId)) })
            addAll(suggestions.map { it.toPeopleUserListItem(PeopleItemType.Suggestion) })
        }.distinctBy { it.id }.filterNot { it.id == session?.myUserId }

        return mutableListOf<PeopleListItem>().apply {
            addSection(
                PeopleHeaderItem.requests,
                uniqueItemsList.filter { it.type == PeopleItemType.Request }
            )
            addSection(
                PeopleHeaderItem.friends,
                uniqueItemsList.filter { it.type == PeopleItemType.Friend }
            )
            addSection(
                PeopleHeaderItem.followingUsersHeader,
                uniqueItemsList.filter { it.type == PeopleItemType.Following }
            )
            addSection(
                PeopleHeaderItem.followersUsersHeader,
                uniqueItemsList.filter { it.type == PeopleItemType.Follower }
            )
            addSection(
                PeopleHeaderItem.knownUsersHeader,
                uniqueItemsList.filter { it.type == PeopleItemType.Known }
            )
            addSection(
                PeopleHeaderItem.suggestions,
                uniqueItemsList.filter { it.type == PeopleItemType.Suggestion }
            )
            addSection(
                PeopleHeaderItem.ignoredUsers,
                uniqueItemsList.filter { it.type == PeopleItemType.Ignored }
            )
        }
    }

    private fun getKnownUserItemType(userId: String): PeopleItemType {
        val isFollower = isMyFollower(userId)
        val amIFollowing = amIFollowing(userId)
        val isFriend = isFollower && amIFollowing

        return when {
            isFriend -> PeopleItemType.Friend
            amIFollowing -> PeopleItemType.Following
            isFollower -> PeopleItemType.Follower
            else -> PeopleItemType.Known
        }
    }

    private fun MutableList<PeopleListItem>.addSection(
        title: PeopleHeaderItem,
        items: List<PeopleListItem>
    ) {
        if (items.isNotEmpty()) {
            add(title)
            addAll(items)
        }
    }

    private fun isMyFollower(userId: String): Boolean {
        val mySharedCircleMembers = getSharedCirclesSpaceId()?.let {
            session?.getRoom(it)?.roomSummary()?.otherMemberIds
        } ?: emptyList()
        return mySharedCircleMembers.contains(userId)
    }

    private fun amIFollowing(userId: String) = getSharedCircleFor(userId) != null

}