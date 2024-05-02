package org.futo.circles.core.feature.room.well_known

import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.RoomPublicInfo
import org.futo.circles.core.model.RoomUrlData
import org.futo.circles.core.model.toRoomPublicInfo
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import javax.inject.Inject

class RoomWellKnownDataSource @Inject constructor() {

    val session by lazy { MatrixSessionProvider.getSessionOrThrow() }

    suspend fun resolveRoom(roomUrlData: RoomUrlData): Response<RoomPublicInfo> = createResult {
        session.getRoom(roomUrlData.roomId)?.roomSummary()?.toRoomPublicInfo(roomUrlData.type)
            ?.let { return@createResult it }
        when (val peekResult = session.roomService().peekRoom(roomUrlData.roomId)) {
            is PeekResult.Success -> peekResult.toRoomPublicInfo(roomUrlData.type)
            is PeekResult.PeekingNotAllowed -> roomUrlData.toRoomPublicInfo()
            PeekResult.UnknownAlias -> throw IllegalArgumentException("Room not found")
        }
    }
}