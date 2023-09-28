package org.futo.circles.core.room

import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.workspace.SpacesTreeAccountDataSource
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

class RoomRelationsBuilder @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun setRelations(childId: String, parentRoomId: String) {
        val via = listOf(getHomeServerDomain())
        tryOrNull {
            session?.spaceService()?.setSpaceParent(childId, parentRoomId, false, via)
        }
        getJoinedRoomById(parentRoomId)?.asSpace()?.addChildren(childId, via, null)
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

    suspend fun setInvitedRoomRelations(roomId: String, circlesRoom: CirclesRoom) {
        val key = circlesRoom.parentAccountDataKey ?: return
        val parentId = spacesTreeAccountDataSource.getRoomIdByKey(key) ?: return
        setRelations(roomId, parentId)
    }

    suspend fun setInvitedRoomRelations(roomId: String, parentCircleId: String) {
        setRelations(roomId, parentCircleId)
    }

    private fun getHomeServerDomain() = session?.sessionParams?.homeServerHost ?: ""

}