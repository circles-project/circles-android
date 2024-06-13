package org.futo.circles.core.feature.room.invites

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.mapping.toRoomInviteListItem
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.InviteTypeArg
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.model.convertToCircleRoomType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getAllRoomsLiveData
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class RoomRequestsDataSource @Inject constructor() {

    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getRoomInvitesFlow(
        inviteType: InviteTypeArg
    ): Flow<List<RoomInviteListItem>> = combine(
        getAllRoomsLiveData(listOf(Membership.INVITE)).asFlow(),
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
                }
            }.map {
                it.toRoomInviteListItem(
                    convertToCircleRoomType(it.roomType),
                    shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
                )
            }
        }
    }.distinctUntilChanged()


    fun getKnockRequestFlow() {

    }

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
}