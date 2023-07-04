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
import org.futo.circles.model.RoomUrlData
import org.futo.circles.model.UserPublicInfo
import org.futo.circles.model.UserUrlData
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

    suspend fun resolveRoom(roomUrlData: RoomUrlData): Response<RoomPublicInfo> = createResult {
        session.getRoom(roomUrlData.roomId)?.roomSummary()?.toRoomPublicInfo()
            ?.let { return@createResult it }
        when (val peekResult = session.roomService().peekRoom(roomUrlData.roomId)) {
            is PeekResult.Success -> peekResult.toRoomPublicInfo()
            is PeekResult.PeekingNotAllowed -> roomUrlData.toRoomPublicInfo()
            PeekResult.UnknownAlias -> throw IllegalArgumentException(context.getString(R.string.room_not_found))
        }
    }

    suspend fun resolveUser(userUrlData: UserUrlData): Response<UserPublicInfo> =
        createResult {
            val roomInfo = tryOrNull {
                (resolveRoom(
                    RoomUrlData(userUrlData.sharedSpaceId, userUrlData.sharedSpaceId, null)
                ) as? Response.Success)?.data
            }
            val user = session.getOrFetchUser(userUrlData.userId)
            UserPublicInfo(
                id = userUrlData.userId,
                displayName = user.notEmptyDisplayName(),
                avatarUrl = user.avatarUrl,
                sharedSpaceId = userUrlData.sharedSpaceId,
                memberCount = roomInfo?.memberCount ?: 0,
                membership = roomInfo?.membership ?: Membership.NONE
            )
        }
}