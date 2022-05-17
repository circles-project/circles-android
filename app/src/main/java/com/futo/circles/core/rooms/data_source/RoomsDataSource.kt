package com.futo.circles.core.rooms.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.model.RoomListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary

abstract class RoomsDataSource {

    protected val session = MatrixSessionProvider.currentSession

    abstract fun filterRooms(list: List<RoomSummary>): List<RoomListItem>

    suspend fun rejectInvite(roomId: String) = createResult {
        session?.roomService()?.leaveRoom(roomId)
    }
}