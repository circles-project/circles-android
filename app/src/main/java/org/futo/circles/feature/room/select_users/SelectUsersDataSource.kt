package org.futo.circles.feature.room.select_users

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.extensions.*
import org.futo.circles.mapping.toUserListItem
import org.futo.circles.model.HeaderItem
import org.futo.circles.model.InviteMemberListItem
import org.futo.circles.model.NoResultsItem
import org.futo.circles.model.UserListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.user.model.User

class SelectUsersDataSource(roomId: String?) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId ?: "")

    private val existingMembersIds = mutableListOf<String>().apply {
        addAll(session?.getUserIdsToExclude() ?: emptySet())
        addAll(room?.membershipService()?.getRoomMembers(
            roomMemberQueryParams {
                memberships = listOf(Membership.JOIN, Membership.INVITE, Membership.BAN)
            }
        )?.map { it.userId } ?: emptySet())
    }.toSet()

    val selectedUsersFlow = MutableStateFlow<List<UserListItem>>(emptyList())

    suspend fun loadAllRoomMembersIfNeeded() {
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams())?.forEach {
            session.getRoom(it.roomId)?.membershipService()?.loadRoomMembersIfNeeded()
        }
    }

    suspend fun search(query: String) =
        combine(
            searchKnownUsers(query),
            searchSuggestions(query),
            selectedUsersFlow
        )
        { knowUsers, suggestions, selectedUsers ->
            buildList(knowUsers, suggestions, selectedUsers)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()


    private fun searchKnownUsers(query: String) = session?.getKnownUsersLive()?.asFlow()
        ?.map {
            it.filter { user ->
                val containsInName = user.displayName?.contains(query, true) ?: false
                val containsInId = user.userId.contains(query, true)
                val notInExistingMembers = existingMembersIds.contains(user.userId).not()
                (containsInName || containsInId) && notInExistingMembers
            }
        } ?: flowOf()


    private suspend fun searchSuggestions(query: String): Flow<List<User>> = flow {
        val userFromDirectory = searchInUsersDirectory(query)
        val userById = searchUserById(query)
        val list = userFromDirectory.toMutableList().apply { userById?.let { add(it) } }
        emit(list.distinctBy { it.userId })
    }

    private suspend fun searchUserById(query: String) = (createResult {
        session?.userService()?.resolveUser(convertQueryToUserId(query))
    } as? Response.Success)?.data

    private suspend fun searchInUsersDirectory(query: String): List<User> {
        val usersByQuery = launchDirectorySearch(query)
        val usersById = launchDirectorySearch(convertQueryToUserId(query))
        return mutableListOf<User>().apply {
            addAll(usersByQuery)
            addAll(usersById)
        }
    }

    private suspend fun launchDirectorySearch(query: String) = session?.userService()
        ?.searchUsersDirectory(query, MAX_SUGGESTION_COUNT, existingMembersIds)?.toMutableList()
        ?: mutableListOf()

    private fun convertQueryToUserId(query: String): String {
        var userId: String? = null
        if (!query.startsWith("@")) userId = "@$query"
        val domain = session?.getServerDomain() ?: ""
        if (!query.contains(":")) userId += ":$domain"
        return userId ?: query
    }

    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        selectedUsers: List<UserListItem>
    ): List<InviteMemberListItem> {
        val list = mutableListOf<InviteMemberListItem>()
        if (knowUsers.isNotEmpty()) {
            list.add(HeaderItem.knownUsersHeader)
            list.addAll(knowUsers.map { knownUser ->
                knownUser.toUserListItem(selectedUsers.containsWithId(knownUser.userId))
            })
        }
        val knowUsersIds = knowUsers.map { it.userId }
        val filteredSuggestion = suggestions.filterNot {
            knowUsersIds.contains(it.userId) || existingMembersIds.contains(it.userId)
        }
        if (filteredSuggestion.isNotEmpty()) {
            list.add(HeaderItem.suggestionHeader)
            list.addAll(filteredSuggestion.map { suggestion ->
                suggestion.toUserListItem(selectedUsers.containsWithId(suggestion.userId))
            })
        }
        if (list.isEmpty()) list.add(NoResultsItem())
        return list
    }

    private fun List<UserListItem>.containsWithId(id: String) =
        firstOrNull { it.id == id } != null

    fun toggleUserSelect(user: UserListItem) {
        val list = selectedUsersFlow.value.toMutableList()
        if (user.isSelected) list.removeIf { it.id == user.id }
        else list.add(user.copy(isSelected = true))
        selectedUsersFlow.value = list
    }


    private companion object {
        private const val MAX_SUGGESTION_COUNT = 30
    }
}