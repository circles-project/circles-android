package org.futo.circles.feature.room.well_known

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.toRoomPublicInfo
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import javax.inject.Inject

class RoomWellKnownDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun resolveRoomById(roomId: String): Response<RoomPublicInfo> = createResult {
        val session = MatrixSessionProvider.currentSession
            ?: throw IllegalArgumentException(context.getString(R.string.session_is_not_created))

        session.getRoom(roomId)?.roomSummary()?.toRoomPublicInfo()?.let { return@createResult it }

        when (val peekResult = session.roomService().peekRoom(roomId)) {
            is PeekResult.Success -> peekResult.toRoomPublicInfo()
            is PeekResult.PeekingNotAllowed -> throw IllegalArgumentException(context.getString(R.string.not_allowed_to_view_room_info))
            PeekResult.UnknownAlias -> throw IllegalArgumentException(context.getString(R.string.room_not_found))
        }
    }

}