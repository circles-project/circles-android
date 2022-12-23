package org.futo.circles.feature.room.select_users

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.extensions.getUserIdsToExclude
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

class SelectUsersDataSource(
    roomId: String?,
    private val searchUserDataSource: SearchUserDataSource
) {

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
            searchUserDataSource.searchKnownUsers(query, existingMembersIds),
            searchUserDataSource.searchSuggestions(query, existingMembersIds),
            selectedUsersFlow
        )
        { knowUsers, suggestions, selectedUsers ->
            buildList(knowUsers, suggestions, selectedUsers)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()


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

}