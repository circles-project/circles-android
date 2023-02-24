package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.getSharedCirclesSpaceId
import org.futo.circles.feature.room.select_users.SearchUserDataSource
import org.futo.circles.mapping.toPeopleIgnoredUserListItem
import org.futo.circles.mapping.toPeopleRequestUserListItem
import org.futo.circles.mapping.toPeopleSuggestionUserListItem
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsFilter
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.user.model.User

class PeopleDataSource(
    private val searchUserDataSource: SearchUserDataSource
) {

    private val session = MatrixSessionProvider.currentSession
    private val profileRoomId = getSharedCirclesSpaceId() ?: ""

    //TODO implement this when we will be able to store shared circles id in profile data
    suspend fun followUser(userId: String) =
        createResult { session?.roomService()?.knock("") }

    suspend fun acceptFollowRequest(userId: String) =
        createResult {
            session?.roomService()?.getRoom(profileRoomId)?.membershipService()?.invite(userId)
        }

    suspend fun declineFollowRequest(userId: String) =
        createResult { session?.getRoom(profileRoomId)?.membershipService()?.remove(userId) }

    private fun getProfileRoomMembersKnockFlow(): Flow<List<User>> =
        session?.getRoom(profileRoomId)?.membershipService()
            ?.getRoomMembersLive(roomMemberQueryParams { memberships = listOf(Membership.KNOCK) })
            ?.map { it.map { User(it.userId, it.displayName, it.avatarUrl) } }
            ?.asFlow()
            ?: flowOf()

    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow(),
        getProfileRoomMembersKnockFlow()
    )
    { knowUsers, suggestions, ignoredUsers, requests ->
        buildList(knowUsers, suggestions, ignoredUsers, requests)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()

    //Todo profile space
    private suspend fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>,
        requests: List<User>
    ): List<PeopleListItem> {
        val uniqueItemsList = mutableListOf<PeopleListItem>().apply {
            addAll(ignoredUsers.map { it.toPeopleIgnoredUserListItem() })
            addAll(requests.map { it.toPeopleRequestUserListItem() })
            addAll(knowUsers.map {
                it.toPeopleSuggestionUserListItem(true, null)
            })
            addAll(
                suggestions.map {
                    it.toPeopleSuggestionUserListItem(false, null)
                }
            )
        }.distinctBy { it.id }

        val displayList = mutableListOf<PeopleListItem>()

        val requestsItems = uniqueItemsList.filterIsInstance<PeopleRequestUserListItem>()
        if (requestsItems.isNotEmpty()) {
            displayList.add(PeopleHeaderItem.requests)
            displayList.addAll(requestsItems)
        }

        val followingItems = uniqueItemsList.filterIsInstance<PeopleFollowingUserListItem>()
        if (followingItems.isNotEmpty()) {
            displayList.add(PeopleHeaderItem.followingUsersHeader)
            displayList.addAll(followingItems)
        }

        val knownItems = uniqueItemsList.filter { it is PeopleSuggestionUserListItem && it.isKnown }
        if (knownItems.isNotEmpty()) {
            displayList.add(PeopleHeaderItem.knownUsersHeader)
            displayList.addAll(knownItems)
        }

        val suggestionsItems =
            uniqueItemsList.filter { it is PeopleSuggestionUserListItem && !it.isKnown }
        if (suggestionsItems.isNotEmpty()) {
            displayList.add(PeopleHeaderItem.suggestions)
            displayList.addAll(suggestionsItems)
        }

        val ignoredItems = uniqueItemsList.filterIsInstance<PeopleIgnoredUserListItem>()
        if (ignoredItems.isNotEmpty()) {
            displayList.add(PeopleHeaderItem.ignoredUsers)
            displayList.addAll(ignoredItems)
        }

        return displayList
    }

}