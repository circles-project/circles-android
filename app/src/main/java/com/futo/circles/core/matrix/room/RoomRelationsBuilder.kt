package com.futo.circles.core.matrix.room

import com.futo.circles.BuildConfig
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room

class RoomRelationsBuilder {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun setRelations(childId: String, parentRoom: Room) {
        val via = listOf(getHomeServerDomain())
        session?.spaceService()?.setSpaceParent(childId, parentRoom.roomId, true, via)
        parentRoom.asSpace()?.addChildren(childId, via, null)
    }

    suspend fun removeRelations(childId: String, parentId: String) {
        session?.getRoom(parentId)?.asSpace()?.removeChildren(childId)
    }

    suspend fun removeAllRelations(childId: String) {
        session?.getRoom(childId)?.roomSummary()?.spaceParents?.forEach {
            val parentId = it.roomSummary?.roomId ?: ""
            session?.getRoom(parentId)?.asSpace()?.removeChildren(childId)
        }
    }

    private fun getHomeServerDomain() = BuildConfig.MATRIX_HOME_SERVER_URL
        .substringAfter("//").replace("/", "")

}