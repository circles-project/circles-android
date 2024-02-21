package org.futo.circles.feature.people.category

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.mapping.toPeopleIgnoredListItem
import org.futo.circles.mapping.toPeopleUserListItem
import org.futo.circles.model.PeopleCategoryTypeArg
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.user.model.User
import javax.inject.Inject

class PeopleCategoryDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    fun getUsersListByCategoryFlow(categoryType: PeopleCategoryTypeArg) = when (categoryType) {
        PeopleCategoryTypeArg.Ignored -> getIgnoredUserFlow().map { list -> list.map { it.toPeopleIgnoredListItem() } }
        else -> combine(
            session.getKnownUsersFlow(),
            getIgnoredUserFlow()
        ) { knowUsers, ignoredUsers ->
            val ignoreUserIds = ignoredUsers.map { it.userId }
            val userByCategory = when (categoryType) {
                PeopleCategoryTypeArg.Connections -> getMyConnections(knowUsers)
                PeopleCategoryTypeArg.Followers -> getFollowers()
                PeopleCategoryTypeArg.Following -> getPeopleImFollowing()
                else -> getOtherUsers(
                    knowUsers,
                    getMyConnections(knowUsers),
                    getFollowers(),
                    getPeopleImFollowing()
                )
            }
            userByCategory.map { it.toPeopleUserListItem(ignoreUserIds.contains(it.userId)) }
        }
    }


    fun getProfileRoomId() = sharedCircleDataSource.getSharedCirclesSpaceId() ?: ""

    fun getIgnoredUserFlow() = session.userService().getIgnoredUsersLive().asFlow()

    //All the joined members (except me) in all of my circle timeline rooms
    fun getFollowers(): List<User> {
        val myCirclesSpace = getMyCirclesSpaceSummary() ?: return emptyList()
        val myTimelinesFollowersIds = myCirclesSpace.spaceChildren?.mapNotNull {
            getTimelineRoomFor(it.childRoomId)?.roomSummary()?.otherMemberIds
        }?.flatMap { it.toSet() } ?: emptyList()

        return myTimelinesFollowersIds.map { session.getUserOrDefault(it) }
    }

    //All the creators of all the timeline rooms that I'm following in my circles
    fun getPeopleImFollowing(): List<User> {
        val myCirclesSpace = getMyCirclesSpaceSummary() ?: return emptyList()
        val peopleIamFollowingIds = myCirclesSpace.spaceChildren?.mapNotNull {
            getJoinedRoomById(it.childRoomId)?.roomSummary()?.spaceChildren?.mapNotNull {
                getRoomOwner(it.childRoomId)?.userId?.takeIf { it != session.myUserId }
            }
        }?.flatMap { it.toSet() } ?: emptyList()

        return peopleIamFollowingIds.map { session.getUserOrDefault(it) }
    }

    fun getMyConnections(knowUsers: List<User>) = knowUsers.filter { isConnection(it.userId) }

    fun getOtherUsers(
        knowUsers: List<User>,
        connections: List<User>,
        followers: List<User>,
        following: List<User>
    ): List<User> {
        val knownIds = knowUsers.map { it.userId }
        val connectionsIds = connections.map { it.userId }
        val followersUsersIds = followers.map { it.userId }
        val followingUsersIds = following.map { it.userId }

        val otherMemberIds =
            knownIds - connectionsIds.toSet() - followersUsersIds.toSet() - followingUsersIds.toSet()

        return otherMemberIds.map { session.getUserOrDefault(it) }
    }

    private fun isConnection(userId: String) =
        sharedCircleDataSource.getSharedCircleFor(userId) != null

    private fun getMyCirclesSpaceSummary(): RoomSummary? {
        val circlesSpaceId = spacesTreeAccountDataSource.getRoomIdByKey(
            CIRCLES_SPACE_ACCOUNT_DATA_KEY
        ) ?: ""
        return getJoinedRoomById(circlesSpaceId)?.roomSummary()
    }
}