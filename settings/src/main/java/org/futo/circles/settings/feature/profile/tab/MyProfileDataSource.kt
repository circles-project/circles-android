package org.futo.circles.settings.feature.profile.tab

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelines
import org.futo.circles.settings.model.PeopleCategoryData
import org.futo.circles.settings.model.PeopleCategoryType
import org.futo.circles.settings.model.PeopleHeaderItem
import org.futo.circles.settings.model.PeopleListItem
import org.futo.circles.settings.model.PeopleUserListItem
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class MyProfileDataSource @Inject constructor() {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    fun getUsersListByCategoryFlow(): Flow<Map<PeopleCategoryType, PeopleCategoryData>> = combine(
        session.getKnownUsersFlow(),
        getIgnoredUserFlow()
    ) { knowUsers, ignoredUsers ->
        val ignoreUserIds = ignoredUsers.map { it.userId }

        val peopleMap = mutableMapOf<PeopleCategoryType, PeopleCategoryData>()

        val followers = getFollowers().filter { !ignoreUserIds.contains(it.userId) }
        peopleMap[PeopleCategoryType.Followers] = PeopleCategoryData(
            count = followers.size,
            type = PeopleCategoryType.Followers,
            listData = buildListWithHeader(PeopleHeaderItem.followersHeader, followers)
        )

        val following = getPeopleImFollowing().filter { !ignoreUserIds.contains(it.userId) }
        peopleMap[PeopleCategoryType.Following] = PeopleCategoryData(
            count = following.size,
            type = PeopleCategoryType.Following,
            listData = buildListWithHeader(PeopleHeaderItem.followingHeader, following)
        )

        val others = getOtherUsers(
            knowUsers,
            getFollowers(),
            getPeopleImFollowing()
        ).filter { !ignoreUserIds.contains(it.userId) }

        peopleMap[PeopleCategoryType.Other] = PeopleCategoryData(
            count = others.size,
            type = PeopleCategoryType.Other,
            listData = buildListWithHeader(PeopleHeaderItem.suggestionsHeader, others)
        )

        peopleMap
    }

    private fun getIgnoredUserFlow() = session.userService().getIgnoredUsersLive().asFlow()

    //All the joined members (except me) in all of my circle timeline rooms
    private fun getFollowers(): List<User> {
        val myTimelinesFollowersIds = getTimelines(listOf(Membership.JOIN))
            .filter { getRoomOwner(it.roomId)?.userId == session.myUserId }
            .map { it.otherMemberIds }
            .flatMap { it.toList() }.toSet()
        return myTimelinesFollowersIds.map { session.getUserOrDefault(it) }
    }

    //All the creators of all the timeline rooms that I'm following in my circles
    private fun getPeopleImFollowing(): List<User> {
        val peopleIamFollowingIds = getTimelines(listOf(Membership.JOIN))
            .mapNotNull { getRoomOwner(it.roomId)?.userId?.takeIf { it != session.myUserId } }
            .toSet()
        return peopleIamFollowingIds.map { session.getUserOrDefault(it) }
    }

    private fun getOtherUsers(
        knowUsers: List<User>,
        followers: List<User>,
        following: List<User>
    ): List<User> {
        val knownIds = knowUsers.map { it.userId }
        val followersUsersIds = followers.map { it.userId }
        val followingUsersIds = following.map { it.userId }

        val otherMemberIds =
            knownIds - followersUsersIds.toSet() - followingUsersIds.toSet()

        return otherMemberIds.map { session.getUserOrDefault(it) }
    }

    private fun buildListWithHeader(
        headerItem: PeopleHeaderItem,
        users: List<User>
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()
        if (users.isNotEmpty()) {
            list.add(headerItem)
            list.addAll(users.map { PeopleUserListItem(it.toCirclesUserSummary()) })
        }
        return list
    }
}