package org.futo.circles.feature.room.well_known

import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.toRoomPublicInfo
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import javax.inject.Inject

class RoomWellKnownDataSource @Inject constructor() {

    suspend fun resolveRoomById(roomId: String): Response<RoomPublicInfo> = createResult {
        val session = MatrixSessionProvider.currentSession
            ?: throw IllegalArgumentException("session is not created")

        session.getRoom(roomId)?.roomSummary()?.toRoomPublicInfo()?.let { return@createResult it }

        when (val peekResult = session.roomService().peekRoom(roomId)) {
            is PeekResult.Success -> peekResult.toRoomPublicInfo()
            else -> throw IllegalArgumentException("room not found")
        }
    }

}