package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.extensions.getKnownUsersLive
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.user.model.User

class PeopleDataSource {

    private val session = MatrixSessionProvider.currentSession

    fun getPeopleList() = combine(getKnownUsersFlow(), getIgnoredUserFlow())
    { knowUsers, ignoredUsers ->
        buildList(knowUsers, ignoredUsers)
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getKnownUsersFlow() = session?.getKnownUsersLive()?.asFlow() ?: flowOf()

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