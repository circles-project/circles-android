package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.futo.circles.core.feature.room.knoks.KnockRequestsDataSource
import org.futo.circles.core.feature.select_users.SearchUserDataSource
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleRequestNotificationListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class PeopleDataSource @Inject constructor(
    private val searchUserDataSource: SearchUserDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource,
    private val knockRequestsDataSource: KnockRequestsDataSource
) {

    private val session = MatrixSessionProvider.currentSession
    private val profileRoomId = sharedCircleDataSource.getSharedCirclesSpaceId() ?: ""

    private fun getProfileRoomMembersKnockFlow(): Flow<List<KnockRequestListItem>> =
        knockRequestsDataSource.getKnockRequestsListItemsLiveData(profileRoomId)?.asFlow()
            ?: flowOf()

    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow(),
        getProfileRoomMembersKnockFlow()
    ) { knowUsers, suggestions, ignoredUsers, requests ->
        withContext(Dispatchers.IO) { buildList(knowUsers, suggestions, ignoredUsers, requests) }
    }.distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()


    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>,
        requests: List<KnockRequestListItem>
    ): List<PeopleListItem> {
        val ignoredUsersIds = ignoredUsers.map { it.userId }.toSet()
        val uniqueItemsList = mutableListOf<PeopleListItem>().apply {
            addAll(knowUsers.map { it.toPeopleUserListItem(getKnownUserItemType(it.userId)) })
            addAll(suggestions.map { it.toPeopleUserListItem(PeopleItemType.Suggestion) })
        }
            .distinctBy { it.id }
            .filterNot { it.id == session?.myUserId || ignoredUsersIds.contains(it.id) }

        return mutableListOf<PeopleListItem>().apply {
            if (requests.isNotEmpty())
                add(PeopleRequestNotificationListItem(requests.size))

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
        val mySharedCircleMembers =
            session?.getRoom(profileRoomId)?.roomSummary()?.otherMemberIds ?: emptyList()
        return mySharedCircleMembers.contains(userId)
    }

    private fun amIFollowing(userId: String) =
        sharedCircleDataSource.getSharedCircleFor(userId) != null

}