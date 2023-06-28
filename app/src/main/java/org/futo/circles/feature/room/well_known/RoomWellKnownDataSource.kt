package org.futo.circles.feature.room.well_known

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrFetchUser
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.RoomPublicInfo
import org.futo.circles.model.UserPublicInfo
import org.futo.circles.model.toRoomPublicInfo
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.peeking.PeekResult
import javax.inject.Inject

class RoomWellKnownDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val session by lazy {
        MatrixSessionProvider.currentSession
            ?: throw IllegalArgumentException(context.getString(R.string.session_is_not_created))
    }

    suspend fun resolveRoomById(roomId: String): Response<RoomPublicInfo> = createResult {
        session.getRoom(roomId)?.roomSummary()?.toRoomPublicInfo()?.let { return@createResult it }
        when (val peekResult = session.roomService().peekRoom(roomId)) {
            is PeekResult.Success -> peekResult.toRoomPublicInfo()
            is PeekResult.PeekingNotAllowed -> throw IllegalArgumentException(context.getString(R.string.not_allowed_to_view_room_info))
            PeekResult.UnknownAlias -> throw IllegalArgumentException(context.getString(R.string.room_not_found))
        }
    }

    suspend fun resolveUserById(userId: String, roomId: String): Response<UserPublicInfo> =
        createResult {
            val roomInfo = tryOrNull { (resolveRoomById(roomId) as? Response.Success)?.data }
            val user = session.getOrFetchUser(userId)
            UserPublicInfo(
                id = userId,
                displayName = user.notEmptyDisplayName(),
                avatarUrl = user.avatarUrl,
                sharedSpaceId = roomId,
                memberCount = roomInfo?.memberCount ?: 0,
                membership = roomInfo?.membership ?: Membership.NONE
            )
        }


}