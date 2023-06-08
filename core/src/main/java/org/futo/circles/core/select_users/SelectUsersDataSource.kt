package org.futo.circles.core.select_users

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.extensions.getUserIdsToExclude
import org.futo.circles.core.mapping.toUserListItem
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.HeaderItem
import org.futo.circles.core.model.InviteMemberListItem
import org.futo.circles.core.model.NoResultsItem
import org.futo.circles.core.model.UserListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.MatrixPatterns
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

@ViewModelScoped
class SelectUsersDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchUserDataSource: SearchUserDataSource
) {
    private val roomId: String? = savedStateHandle["roomId"]

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

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    suspend fun search(query: String) =
        combine(
            searchUserDataSource.searchKnownUsers(query, existingMembersIds),
            searchUserDataSource.searchSuggestions(query, existingMembersIds),
            selectedUsersFlow
        )
        { knowUsers, suggestions, selectedUsers ->
            buildList(knowUsers, suggestions, selectedUsers, query)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()


    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        selectedUsers: List<UserListItem>,
        query: String
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
        if (list.isEmpty()) handleEmptySearchResult(list, query, selectedUsers)
        return list
    }

    private fun handleEmptySearchResult(
        list: MutableList<InviteMemberListItem>,
        query: String,
        selectedUsers: List<UserListItem>
    ) {
        if (MatrixPatterns.isUserId(query)) {
            list.add(HeaderItem.suggestionHeader)
            list.add(
                UserListItem(
                    CirclesUserSummary(query, UserUtils.removeDomainSuffix(query), ""),
                    selectedUsers.containsWithId(query)
                )
            )
        } else list.add(NoResultsItem())
    }

    private fun List<UserListItem>.containsWithId(id: String) =
        firstOrNull { it.id == id } != null

    fun toggleUserSelect(user: UserListItem) {
        val list = selectedUsersFlow.value.toMutableList()
        if (user.isSelected) list.removeIf { it.id == user.id }
        else list.add(user.copy(isSelected = true))
        selectedUsersFlow.value = list
    }

}