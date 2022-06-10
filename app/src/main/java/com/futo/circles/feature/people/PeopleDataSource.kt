package com.futo.circles.feature.people

import androidx.lifecycle.asFlow
import com.futo.circles.core.DEFAULT_USER_PREFIX
import com.futo.circles.mapping.toPeopleUserListItem
import com.futo.circles.model.PeopleHeaderItem
import com.futo.circles.model.PeopleListItem
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.matrix.android.sdk.api.session.user.model.User

class PeopleDataSource {

    private val session = MatrixSessionProvider.currentSession

    private val excludeUserIds = mutableListOf(
        session?.myUserId ?: "",
        DEFAULT_USER_PREFIX + session?.myUserId?.substringAfter(":")
    ).toSet()

    fun getPeopleList() = combine(getKnownUsersFlow(), getIgnoredUserFlow())
    { knowUsers, ignoredUsers ->
        buildList(knowUsers, ignoredUsers)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getKnownUsersFlow() = session?.userService()?.getUsersLive()?.asFlow()
        ?.map { list -> list.filterNot { user -> excludeUserIds.contains(user.userId) } }
        ?: flowOf()

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()

    private fun buildList(knowUsers: List<User>, ignoredUsers: List<User>): List<PeopleListItem> {
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
        return list
    }
}