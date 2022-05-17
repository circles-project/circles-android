package com.futo.circles.core.matrix.room

import com.futo.circles.BuildConfig
import com.futo.circles.model.Group
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class RoomRelationsBuilder {

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
            removeRelations(childId, parentId)
        }
    }

    suspend fun setInvitedGroupRelations(roomId: String) {
        val circlesRoom = Group()
        session?.getRoom(roomId)?.tagsService()?.addTag(circlesRoom.tag, null)
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

    private fun getHomeServerDomain() = BuildConfig.MATRIX_HOME_SERVER_URL
        .substringAfter("//").replace("/", "")

}