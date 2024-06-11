package org.futo.circles.feature.people

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.feature.select_users.SearchUserDataSource
import org.futo.circles.feature.people.category.PeopleCategoryDataSource
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleCategoryListItem
import org.futo.circles.model.PeopleListItem
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class PeopleDataSource @Inject constructor(
    private val searchUserDataSource: SearchUserDataSource,
    private val peopleCategoryDataSource: PeopleCategoryDataSource
) {


    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        peopleCategoryDataSource.getIgnoredUserFlow()
    ) { knowUsers, suggestions, ignoredUsers ->
        withContext(Dispatchers.IO) {
            buildList(
                query,
                knowUsers,
                suggestions,
                ignoredUsers
            )
        }
    }.distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun buildList(
        query: String,
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> =
        if (query.isNotEmpty()) buildSearchResultsList(knowUsers, suggestions, ignoredUsers)
        else buildCategoriesList(knowUsers, ignoredUsers)


    private fun buildSearchResultsList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()
        val ignoredUsersIds = ignoredUsers.map { it.userId }.toSet()
        val searchResult = (knowUsers + suggestions).distinctBy { it.userId }
        list.addAll(searchResult.map {
            it.toPeopleUserListItem(ignoredUsersIds.contains(it.userId))
        })
        return list
    }

    private fun buildCategoriesList(
        knowUsers: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()
        val followingUsers = peopleCategoryDataSource.getPeopleImFollowing()
        val followersUsers = peopleCategoryDataSource.getFollowers()
        val otherMembers = peopleCategoryDataSource.getOtherUsers(
            knowUsers,
            followersUsers,
            followingUsers
        )

        list.apply {
            add(PeopleCategoryListItem.followingUsers.copy(count = followingUsers.size))
            add(PeopleCategoryListItem.followersUsers.copy(count = followersUsers.size))
            add(PeopleCategoryListItem.others.copy(count = otherMembers.size))
            add(PeopleCategoryListItem.ignored.copy(count = ignoredUsers.size))
        }
        return list
    }

}