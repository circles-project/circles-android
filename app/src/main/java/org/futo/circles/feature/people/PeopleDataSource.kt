package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.feature.room.select_users.SearchUserDataSource
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User

class PeopleDataSource(
    private val searchUserDataSource: SearchUserDataSource
) {

    private val session = MatrixSessionProvider.currentSession

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

    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> {
        val filteredKnownUsers = knowUsers.filterNot { knowUser ->
            ignoredUsers.firstOrNull { ignoredUser ->
                ignoredUser.userId == knowUser.userId
            } != null
        }.map { it.toPeopleUserListItem(false) }

        val list = mutableListOf<PeopleListItem>()
        if (filteredKnownUsers.isNotEmpty()) {
            list.add(PeopleHeaderItem.knownUsersHeader)
            list.addAll(filteredKnownUsers)
        }
        
        if (ignoredUsers.isNotEmpty()) {
            list.add(PeopleHeaderItem.ignoredUsers)
            list.addAll(ignoredUsers.map { it.toPeopleUserListItem(true) })
        }

        val existingIds = list.map { it.id }
        val filteredSuggestion = suggestions.filterNot { existingIds.contains(it.userId) }
        if (filteredSuggestion.isNotEmpty()) {
            list.add(PeopleHeaderItem.suggestions)
            list.addAll(suggestions.map { it.toPeopleUserListItem(false) })
        }
        return list
    }
}