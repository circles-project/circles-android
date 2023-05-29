package org.futo.circles.feature.room

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class LeaveRoomDataSource(
    private val roomId: String,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)

    suspend fun leaveGroup() =
        createResult { session?.roomService()?.leaveRoom(roomId) }

    suspend fun deleteCircle() = createResult {
        room?.roomSummary()?.spaceChildren?.forEach {
            roomRelationsBuilder.removeRelations(it.childRoomId, roomId)
        }
        getTimelineRoomFor(roomId)?.let { timelineRoom ->
            timelineRoom.roomSummary()?.otherMemberIds?.forEach { memberId ->
                timelineRoom.membershipService().ban(memberId)
            }
            session?.roomService()?.leaveRoom(timelineRoom.roomId)
        }
        session?.roomService()?.leaveRoom(roomId)
    }

    suspend fun deleteGroup() = createResult {
        roomRelationsBuilder.removeFromAllParents(roomId)
        val group = session?.getRoom(roomId)
        group?.roomSummary()?.otherMemberIds?.forEach { memberId ->
            group.membershipService().ban(memberId)
        }
        session?.roomService()?.leaveRoom(roomId)
    }

    suspend fun deleteGallery() = createResult {
        session?.getRoom(roomId)?.roomSummary()?.tags?.forEach {
            session.getRoom(roomId)?.tagsService()?.deleteTag(it.name)
        }
        roomRelationsBuilder.removeFromAllParents(roomId)
        session?.roomService()?.leaveRoom(roomId)
    }

    fun canLeaveRoom(): Boolean {
        val isSingleMember =
            room?.membershipService()?.getRoomMembers(roomMemberQueryParams {
                memberships = listOf(Membership.JOIN)
            })?.size == 1
        if (isSingleMember) return true
        val isUserOwner = getCurrentUserPowerLevel(roomId) == Role.Admin.value
        if (!isUserOwner) return true
        val roomHasOneOwner = getRoomOwners(roomId).size == 1
        return !roomHasOneOwner
    }
}