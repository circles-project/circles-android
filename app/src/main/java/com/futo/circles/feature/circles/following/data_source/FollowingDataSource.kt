package com.futo.circles.feature.circles.following.data_source

import android.content.Context
import androidx.lifecycle.map
import com.futo.circles.R
import com.futo.circles.core.matrix.room.RoomRelationsBuilder
import com.futo.circles.extensions.createResult
import com.futo.circles.mapping.toFollowingListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

class FollowingDataSource(
    private val roomId: String,
    context: Context,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    private val room = session.getRoom(roomId) ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    val roomsLiveData = room.getRoomSummaryLive().map {
        val children = it.getOrNull()?.spaceChildren ?: emptyList()
        children.mapNotNull {
            session.getRoom(it.childRoomId)?.roomSummary()?.takeIf { it.membership.isActive() }
                ?.toFollowingListItem(roomId)
        }
    }

    suspend fun removeRoomRelations(childRoomId: String) = createResult {
        roomRelationsBuilder.removeRelations(childRoomId, roomId)
    }

    suspend fun unfollowRoom(childRoomId: String) = createResult {
        roomRelationsBuilder.removeFromAllParents(childRoomId)
        session.roomService().leaveRoom(childRoomId)
    }
}