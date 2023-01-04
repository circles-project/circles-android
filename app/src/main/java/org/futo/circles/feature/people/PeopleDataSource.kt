package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.room.select_users.SearchUserDataSource
import org.futo.circles.mapping.toPeopleFollowingUserListItem
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

    //TODO get profile room id from user's profile
    //61 - !dYyuPbUOkbUYUOMbot:nl.circles-dev.net
    //64 - !fznKPZDngFIyKzprgp:nl.circles-dev.net
    private val profileRoomId = "!dYyuPbUOkbUYUOMbot:nl.circles-dev.net"

    suspend fun followUser(userListItem: PeopleSuggestionUserListItem) =
        createResult { session?.roomService()?.knock(profileRoomId) }

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
                val profileRoomId = getProfileRoomForUser(it.userId)?.roomId
                if (amIFollowThisUserProfile(profileRoomId)) it.toPeopleFollowingUserListItem()
                else it.toPeopleSuggestionUserListItem(true, profileRoomId)
            })
            addAll(
                suggestions.map {
                    it.toPeopleSuggestionUserListItem(
                        false,
                        getProfileRoomForUser(it.userId)?.roomId
                    )
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

    private suspend fun getProfileRoomForUser(userId: String): Room? {
        val publicRoomResult = createResult {
            session?.roomDirectoryService()?.getPublicRooms(
                UserUtils.getServerDomain(userId),
                PublicRoomsParams(limit = 1, filter = PublicRoomsFilter(userId))
            )
        }
        return when (publicRoomResult) {
            is Response.Error -> null
            is Response.Success -> {
                val profileRoomId =
                    publicRoomResult.data?.chunk?.firstOrNull()?.roomId ?: return null
                session?.roomService()?.getRoom(profileRoomId)
            }
        }
    }

    private fun amIFollowThisUserProfile(profileRoomId: String?): Boolean {
        profileRoomId ?: return false
        return session?.roomService()?.getRoom(profileRoomId)?.membershipService()
            ?.getRoomMember(session.myUserId) != null
    }

}