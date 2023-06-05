package org.futo.circles.core.select_users

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.extensions.getServerDomain
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.user.model.User

class SearchUserDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun loadAllRoomMembersIfNeeded() {
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams())?.forEach {
            session.getRoom(it.roomId)?.membershipService()?.loadRoomMembersIfNeeded()
        }
    }

    fun searchKnownUsers(query: String, userIdsToExclude: Set<String> = emptySet()) =
        session?.getKnownUsersFlow()
            ?.map {
                it.filter { user ->
                    val containsInName = user.displayName?.contains(query, true) ?: false
                    val containsInId = user.userId.contains(query, true)
                    val notInExistingMembers = userIdsToExclude.contains(user.userId).not()
                    (containsInName || containsInId) && notInExistingMembers
                }
            } ?: flowOf()

    suspend fun searchSuggestions(
        query: String,
        userIdsToExclude: Set<String> = emptySet()
    ): Flow<List<User>> = flow {
        val userFromDirectory = searchInUsersDirectory(query, userIdsToExclude)
        val userById = searchUserById(query)
        val list = userFromDirectory.toMutableList().apply { userById?.let { add(it) } }
        emit(list.distinctBy { it.userId })
    }

    private suspend fun searchUserById(query: String) = (createResult {
        session?.userService()?.resolveUser(convertQueryToUserId(query))
    } as? Response.Success)?.data

    private suspend fun searchInUsersDirectory(
        query: String,
        userIdsToExclude: Set<String>
    ): List<User> {
        val usersByQuery = launchDirectorySearch(query, userIdsToExclude)
        val usersById = launchDirectorySearch(convertQueryToUserId(query), userIdsToExclude)
        return mutableListOf<User>().apply {
            addAll(usersByQuery)
            addAll(usersById)
        }
    }

    private suspend fun launchDirectorySearch(query: String, userIdsToExclude: Set<String>) =
        session?.userService()
            ?.searchUsersDirectory(query, MAX_SUGGESTION_COUNT, userIdsToExclude.toSet())
            ?.toMutableList()
            ?: mutableListOf()

    private fun convertQueryToUserId(query: String): String {
        var userId: String? = null
        if (!query.startsWith("@")) userId = "@$query"
        val domain = session?.getServerDomain() ?: ""
        if (!query.contains(":")) userId += ":$domain"
        return userId ?: query
    }

    private companion object {
        private const val MAX_SUGGESTION_COUNT = 30
    }

}