package org.futo.circles.feature.people

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.feature.room.knoks.KnockRequestsDataSource
import org.futo.circles.core.feature.select_users.SearchUserDataSource
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getSpacesLiveData
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleCategoryListItem
import org.futo.circles.model.PeopleItemType
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleRequestNotificationListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class PeopleDataSource @Inject constructor(
    private val searchUserDataSource: SearchUserDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource,
    private val knockRequestsDataSource: KnockRequestsDataSource,
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource
) {

    private val session = MatrixSessionProvider.currentSession
    private val profileRoomId = sharedCircleDataSource.getSharedCirclesSpaceId() ?: ""

    private fun getKnockRequestCountFlow(): Flow<Int> =
        knockRequestsDataSource.getKnockRequestsListItemsLiveData(profileRoomId)?.map {
            it.size
        }?.asFlow() ?: flowOf()

    private fun getProfileSpaceInvitesCountFlow() =
        getSpacesLiveData(listOf(Membership.INVITE)).map { it.size }.asFlow()


    suspend fun getPeopleList(query: String) = combine(
        searchUserDataSource.searchKnownUsers(query),
        searchUserDataSource.searchSuggestions(query),
        getIgnoredUserFlow(),
        getKnockRequestCountFlow(),
        getProfileSpaceInvitesCountFlow()
    ) { knowUsers, suggestions, ignoredUsers, knocksCount, profileInvitesCount ->
        withContext(Dispatchers.IO) {
            buildList(
                query,
                knowUsers,
                suggestions,
                ignoredUsers,
                knocksCount,
                profileInvitesCount
            )
        }
    }.distinctUntilChanged()

    suspend fun refreshRoomMembers() {
        searchUserDataSource.loadAllRoomMembersIfNeeded()
    }

    private fun getIgnoredUserFlow() =
        session?.userService()?.getIgnoredUsersLive()?.asFlow() ?: flowOf()


    private fun buildList(
        query: String,
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>,
        knocksCount: Int,
        profileInvitesCount: Int
    ): List<PeopleListItem> =
        if (query.isNotEmpty()) buildSearchResultsList(knowUsers, suggestions, ignoredUsers)
        else buildCategoriesList(knowUsers, ignoredUsers, knocksCount, profileInvitesCount)


    private fun buildSearchResultsList(
        knowUsers: List<User>,
        suggestions: List<User>,
        ignoredUsers: List<User>
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()
        val ignoredUsersIds = ignoredUsers.map { it.userId }.toSet()
        val searchResult = (knowUsers + suggestions).distinctBy { it.userId }
        list.addAll(searchResult.map {
            it.toPeopleUserListItem(
                PeopleItemType.Suggestion,
                ignoredUsersIds.contains(it.userId)
            )
        })
        return list
    }

    private fun buildCategoriesList(
        knowUsers: List<User>,
        ignoredUsers: List<User>,
        knocksCount: Int,
        profileInvitesCount: Int
    ): List<PeopleListItem> {
        val list = mutableListOf<PeopleListItem>()
        val requestsCount = knocksCount + profileInvitesCount
        val knownIds = knowUsers.map { it.userId }
        val followingUsersIds = getPeopleImFollowingIds()
        val followersUsersIds = getFollowersIds()
        val connectionsIds =
            knowUsers.mapNotNull { if (isConnection(it.userId)) it.userId else null }
        val otherMemberIds =
            knownIds - connectionsIds.toSet() - followersUsersIds.toSet() - followingUsersIds.toSet()

        list.apply {
            if (requestsCount > 0) add(PeopleRequestNotificationListItem(requestsCount))
            add(PeopleCategoryListItem.connections.copy(count = connectionsIds.size))
            add(PeopleCategoryListItem.followingUsers.copy(count = followingUsersIds.size))
            add(PeopleCategoryListItem.followersUsers.copy(count = followersUsersIds.size))
            add(PeopleCategoryListItem.others.copy(count = otherMemberIds.size))
            add(PeopleCategoryListItem.ignored.copy(count = ignoredUsers.size))
        }
        return list
    }

    //All the joined members (except me) in all of my circle timeline rooms
    private fun getFollowersIds(): List<String> {
        val myCirclesSpace = getMyCirclesSpaceSummary() ?: return emptyList()
        val myTimelinesFollowers = myCirclesSpace.spaceChildren?.mapNotNull {
            getTimelineRoomFor(it.childRoomId)?.roomSummary()?.otherMemberIds
        }?.flatMap { it.toSet() } ?: emptyList()

        return myTimelinesFollowers
    }

    //All the creators of all the timeline rooms that I'm following in my circles
    private fun getPeopleImFollowingIds(): List<String> {
        val myCirclesSpace = getMyCirclesSpaceSummary() ?: return emptyList()
        val peopleIamFollowing = myCirclesSpace.spaceChildren?.mapNotNull {
            getJoinedRoomById(it.childRoomId)?.roomSummary()?.spaceChildren?.mapNotNull {
                getRoomOwner(it.childRoomId)?.userId?.takeIf { it != session?.myUserId }
            }
        }?.flatMap { it.toSet() } ?: emptyList()

        return peopleIamFollowing
    }

    private fun getMyCirclesSpaceSummary(): RoomSummary? {
        val circlesSpaceId = spacesTreeAccountDataSource.getRoomIdByKey(
            CIRCLES_SPACE_ACCOUNT_DATA_KEY
        ) ?: ""
        return getJoinedRoomById(circlesSpaceId)?.roomSummary()
    }

    private fun isConnection(userId: String) =
        sharedCircleDataSource.getSharedCircleFor(userId) != null

}