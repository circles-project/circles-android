package org.futo.circles.core.feature.room.invites

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.feature.room.knoks.KnockRequestsDataSource
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.model.ConnectionInviteListItem
import org.futo.circles.core.model.FollowRequestListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.InviteHeader
import org.futo.circles.core.model.InviteListItem
import org.futo.circles.core.model.InviteTypeArg
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.model.convertToCircleRoomType
import org.futo.circles.core.model.toCircleUser
import org.futo.circles.core.model.toRoomInviteListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getAllCirclesRoomsLiveData
import org.futo.circles.core.utils.getSpacesLiveData
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class InvitesDataSource @Inject constructor(
    private val knockRequestsDataSource: KnockRequestsDataSource,
    sharedCircleDataSource: SharedCircleDataSource
) {

    private val session = MatrixSessionProvider.currentSession
    private val profileRoomId = sharedCircleDataSource.getSharedCirclesSpaceId() ?: ""
    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getInvitesFlow(type: InviteTypeArg) = when (type) {
        InviteTypeArg.People -> getPeopleInvites()
        else -> getRoomInvitesFlow(type)
    }

    private fun getPeopleInvites() = combine(
        getProfileSpaceInvitesFlow(),
        getProfileRoomMembersKnockFlow()
    ) { invites, knocks ->
        val list = mutableListOf<InviteListItem>()
        if (invites.isNotEmpty()) {
            list.add(InviteHeader.connectInvitesHeader)
            list.addAll(invites)
        }
        if (knocks.isNotEmpty()) {
            list.add(InviteHeader.followRequestHeader)
            list.addAll(knocks)
        }
        list
    }

    private fun getProfileRoomMembersKnockFlow(): Flow<List<FollowRequestListItem>> =
        knockRequestsDataSource.getKnockRequestsListItemsLiveData(profileRoomId)?.map { list ->
            list.map { FollowRequestListItem(it.toCircleUser(), it.message) }
        }?.asFlow() ?: flowOf()

    private fun getProfileSpaceInvitesFlow(): Flow<List<ConnectionInviteListItem>> =
        getSpacesLiveData(listOf(Membership.INVITE)).map {
            it.filter { it.roomType == RoomType.SPACE }.map { summary ->
                ConnectionInviteListItem(
                    summary.roomId,
                    session.getUserOrDefault(summary.inviterId ?: "").toCirclesUserSummary()
                )
            }
        }.asFlow()

    private fun getRoomInvitesFlow(
        inviteType: InviteTypeArg
    ): Flow<List<RoomInviteListItem>> = combine(
        getAllCirclesRoomsLiveData(listOf(Membership.INVITE)).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().getKnownUsersFlow(),
        roomIdsToUnblurProfile
    ) { roomSummaries, knownUsers, roomIdsToUnblur ->
        withContext(Dispatchers.IO) {
            val knownUsersIds = knownUsers.map { it.userId }.toSet()
            roomSummaries.filter {
                when (inviteType) {
                    InviteTypeArg.Circle -> it.roomType == TIMELINE_TYPE
                    InviteTypeArg.Group -> it.roomType == GROUP_TYPE
                    InviteTypeArg.Photo -> it.roomType == GALLERY_TYPE
                    else -> false
                }
            }.map {
                it.toRoomInviteListItem(
                    convertToCircleRoomType(it.roomType),
                    shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
                )
            }
        }
    }.distinctUntilChanged()

    private fun shouldBlurIconFor(
        roomSummary: RoomSummary,
        knownUserIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): Boolean {
        val isKnownUser = knownUserIds.contains(roomSummary.inviterId)
        val isRoomUnbluredByUser = roomIdsToUnblur.contains(roomSummary.roomId)
        val hasIcon = roomSummary.avatarUrl.isNotEmpty()
        return !isKnownUser && !isRoomUnbluredByUser && hasIcon
    }

    fun unblurProfileImageFor(id: String) {
        roomIdsToUnblurProfile.update { set -> set.toMutableSet().apply { add(id) } }
    }

    suspend fun acceptFollowRequest(userId: String) = createResult {
        session?.roomService()?.getRoom(profileRoomId)?.membershipService()?.invite(userId)
    }

    suspend fun declineFollowRequest(userId: String) =
        createResult { session?.getRoom(profileRoomId)?.membershipService()?.remove(userId) }
}