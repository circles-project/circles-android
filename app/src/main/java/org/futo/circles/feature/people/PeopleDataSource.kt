package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.room.select_users.SearchUserDataSource
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsFilter
import org.matrix.android.sdk.api.session.room.model.roomdirectory.PublicRoomsParams
import org.matrix.android.sdk.api.session.user.model.User

class PeopleDataSource(
    private val searchUserDataSource: SearchUserDataSource
) {

    private val session = MatrixSessionProvider.currentSession

    //61 - !dYyuPbUOkbUYUOMbot:nl.circles-dev.net
    //64 - !fznKPZDngFIyKzprgp:nl.circles-dev.net
    suspend fun followUser(userListItem: PeopleUserListItem) =
        createResult {
            session?.roomService()?.knock("!dYyuPbUOkbUYUOMbot:nl.circles-dev.net")
        }

    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow()
    )
    { knowUsers, suggestions, ignoredUsers ->
        buildList(knowUsers, suggestions, ignoredUsers)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()

    private suspend fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()

        val filteredKnownUsers = getKnownPeopleWithoutIgnored(knowUsers, ignoredUsers)

        val followingUsers = filteredKnownUsers.filter { it.isFollowedByMe }
        if (followingUsers.isNotEmpty()) {
            list.add(PeopleHeaderItem.followingUsersHeader)
            list.addAll(followingUsers)
        }

        val known = filteredKnownUsers.filterNot { it.isFollowedByMe }
        if (known.isNotEmpty()) {
            list.add(PeopleHeaderItem.knownUsersHeader)
            list.addAll(known)
        }

        if (ignoredUsers.isNotEmpty()) {
            list.add(PeopleHeaderItem.ignoredUsers)
            list.addAll(ignoredUsers.map { it.toPeopleUserListItem(isIgnored = true) })
        }

        val existingIds = list.map { it.id }
        val filteredSuggestion = suggestions.filterNot { existingIds.contains(it.userId) }
        if (filteredSuggestion.isNotEmpty()) {
            list.add(PeopleHeaderItem.suggestions)
            list.addAll(filteredSuggestion.map {
                it.toPeopleUserListItem(profileRoomId = getProfileRoomForUser(it.userId)?.roomId)
            })
        }
        return list
    }

    private suspend fun getKnownPeopleWithoutIgnored(
        knowUsers: List<User>,
        ignoredUsers: List<User>
    ) = knowUsers.filterNot { knowUser ->
        ignoredUsers.firstOrNull { ignoredUser -> ignoredUser.userId == knowUser.userId } != null
    }.map {
        val profileRoom = getProfileRoomForUser(it.userId)
        val isFollowedByMe = profileRoom?.let { amIFollowThisUserProfile(it.roomId) } ?: false
        it.toPeopleUserListItem(profileRoom?.roomId, false, isFollowedByMe)
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

    private fun amIFollowThisUserProfile(profileRoomId: String) =
        session?.roomService()?.getRoom(profileRoomId)?.membershipService()
            ?.getRoomMember(session.myUserId) != null
}