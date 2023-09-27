package org.futo.circles.core.select_users

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrFetchUser
import org.futo.circles.core.extensions.getServerDomain
import org.futo.circles.core.extensions.getUserIdsToExclude
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class SearchUserDataSource @Inject constructor() {

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

    private fun Session.getKnownUsersFlow() =
        roomService().getRoomSummariesLive(roomSummaryQueryParams { excludeType = null }).asFlow()
            .mapLatest { roomSummaries ->
                val knowUsers = mutableSetOf<User>()
                roomSummaries.forEach { summary ->
                    summary.otherMemberIds.forEach { knowUsers.add(getOrFetchUser(it)) }
                }
                knowUsers.toList().filterNot { getUserIdsToExclude().contains(it.userId) }
            }

    suspend fun searchSuggestions(
        query: String,
        userIdsToExclude: Set<String> = emptySet()
    ): Flow<List<User>> = flow {
        val userFromDirectory = searchInUsersDirectory(query, userIdsToExclude)
        val userById = searchUserById(query)
        val list = userFromDirectory.toMutableList().apply { userById?.let { add(it) } }
        emit(list.distinctBy { it.userId })
    }.catch { emit(emptyList()) }

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
        val domain = session?.getServerDomain() ?: ""
        return if (query.contains(":")) query else
            if (query.startsWith("@")) "$query:$domain" else "@$query:$domain"
    }

    private companion object {
        private const val MAX_SUGGESTION_COUNT = 30
    }

}