package org.futo.circles.feature.direct.create

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.feature.select_users.SearchUserDataSource
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getAllDirectMessagesLiveData
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class CreateDMDataSource @Inject constructor(
    private val searchUserDataSource: SearchUserDataSource
) {

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    suspend fun search(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow(),
        getAllDirectMessagesLiveData().asFlow()
    ) { knowUsers, suggestions, ignoredUsers, directRoomsSummary ->
        withContext(Dispatchers.IO) {
            buildList(
                knowUsers,
                suggestions,
                ignoredUsers,
                directRoomsSummary
            )
        }
    }.distinctUntilChanged()

    private fun buildList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>,
        directRoomsSummary: List<RoomSummary>
    ): List<CirclesUserSummary> {
        val invitedOrActiveDirect = directRoomsSummary.map { it.directUserId }.toSet()
        val ignoredUsersIds = ignoredUsers.map { it.userId }.toSet()

        val usersToExclude =
            invitedOrActiveDirect + ignoredUsersIds + setOf(MatrixSessionProvider.getSessionOrThrow().myUserId)
        val searchResult = (knowUsers + suggestions).distinctBy { it.userId }

        return searchResult.mapNotNull { if (!usersToExclude.contains(it.userId)) it.toCirclesUserSummary() else null }
    }

    private fun getIgnoredUserFlow() =
        MatrixSessionProvider.getSessionOrThrow().userService().getIgnoredUsersLive().asFlow()

}