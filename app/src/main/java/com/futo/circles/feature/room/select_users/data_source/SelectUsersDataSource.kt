package com.futo.circles.feature.room.select_users.data_source

import androidx.lifecycle.asFlow
import com.futo.circles.mapping.toUserListItem
import com.futo.circles.model.HeaderItem
import com.futo.circles.model.InviteMemberListItem
import com.futo.circles.model.NoResultsItem
import com.futo.circles.model.UserListItem
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.user.model.User

class SelectUsersDataSource(roomId: String?) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId ?: "")

    private val existingMembersIds = mutableListOf<String>().apply {
        session?.myUserId?.let {
            add(it)
            add(DEFAULT_USER_PREFIX + it.substringAfter(":"))
        }
        room?.roomSummary()?.otherMemberIds?.let { addAll(it) }
    }.toSet()

    val selectedUsersFlow = MutableStateFlow<List<UserListItem>>(emptyList())


    suspend fun search(query: String) =
        combine(searchKnownUsers(query), searchSuggestions(query), selectedUsersFlow)
        { knowUsers, suggestions, selectedUsers ->
            buildList(knowUsers, suggestions, selectedUsers)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()


    private fun searchKnownUsers(query: String) = session?.userService()?.getUsersLive()?.asFlow()
        ?.map { list ->
            list.filterNot { user -> existingMembersIds.contains(user.userId) }
                .filter { user ->
                    (user.displayName?.contains(query, true) ?: false
                            || user.userId.contains(query, true))
                            && existingMembersIds.contains(user.userId).not()
                }
        } ?: flowOf()


    private suspend fun searchSuggestions(query: String): Flow<List<User>> = flow {
        val users = session?.userService()
            ?.searchUsersDirectory(query, MAX_SUGGESTION_COUNT, existingMembersIds)
        emit(users ?: emptyList())
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

    fun toggleUserSelect(user: UserListItem) {
        val list = selectedUsersFlow.value.toMutableList()

        if (user.isSelected) list.removeIf { it.id == user.id }
        else list.add(user.copy(isSelected = true))
        selectedUsersFlow.value = list
    }

    private fun List<UserListItem>.containsWithId(id: String) = firstOrNull { it.id == id } != null


    private companion object {
        private const val MAX_SUGGESTION_COUNT = 30
        private const val DEFAULT_USER_PREFIX = "@notices:"
    }
}