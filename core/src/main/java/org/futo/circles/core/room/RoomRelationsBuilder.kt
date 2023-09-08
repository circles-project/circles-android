package org.futo.circles.core.room

import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.Group
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class RoomRelationsBuilder @Inject constructor() {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun setRelations(childId: String, parentRoom: Room, isRoomCreatedByMe: Boolean = true) {
        val via = listOf(getHomeServerDomain())
        if (isRoomCreatedByMe) {
            session?.spaceService()?.setSpaceParent(childId, parentRoom.roomId, true, via)
        }
        parentRoom.asSpace()?.addChildren(childId, via, null)
    }

    suspend fun removeRelations(childId: String, parentId: String) {
        session?.spaceService()?.removeSpaceParent(childId, parentId)
        session?.getRoom(parentId)?.asSpace()?.removeChildren(childId)
    }

    suspend fun removeFromAllParents(childId: String) {
        session?.getRoom(childId)?.roomSummary()?.spaceParents?.forEach {
            val parentId = it.roomSummary?.roomId ?: ""
            if (getRoomOwners(parentId).firstOrNull { it.userId == session?.myUserId } != null)
                removeRelations(childId, parentId)
        }
    }

    suspend fun setInvitedGroupRelations(roomId: String) =
        setInvitedRoomRelations(roomId, Group())

    suspend fun setInvitedGalleryRelations(roomId: String) =
        setInvitedRoomRelations(roomId, Gallery())

    private suspend fun setInvitedRoomRelations(roomId: String, circlesRoom: CirclesRoom) {
        circlesRoom.tag?.let { session?.getRoom(roomId)?.tagsService()?.addTag(it, null) }
        circlesRoom.parentTag?.let { tag ->
            findRoomByTag(tag)
                ?.let { room -> setRelations(roomId, room, false) }
        }
    }

    suspend fun setInvitedCircleRelations(roomId: String, parentCircleId: String) {
        session?.getRoom(parentCircleId)?.let { parentCircle ->
            setRelations(roomId, parentCircle, false)
        }
    }

    fun findRoomByTag(tag: String): Room? {
        val roomWithTagId =
            session?.roomService()?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
                ?.firstOrNull { summary -> summary.tags.firstOrNull { it.name == tag } != null }
                ?.roomId
        return roomWithTagId?.let { session?.getRoom(it) }
    }

    private fun getHomeServerDomain() = session?.sessionParams?.homeServerHost ?: ""

}