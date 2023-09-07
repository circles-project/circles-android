package org.futo.circles.core.room.circles.following

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.CIRCLE_TAG
import org.futo.circles.core.model.toFollowingListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

@ViewModelScoped
class FollowingDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    private val session = MatrixSessionProvider.getSessionOrThrow()
    private val room = session.getRoom(roomId) ?: throw IllegalArgumentException("room not found")

    val roomsLiveData = room.getRoomSummaryLive().map {
        val children = it.getOrNull()?.spaceChildren ?: emptyList()
        children.mapNotNull {
            session.getRoom(it.childRoomId)?.roomSummary()?.takeIf { it.membership.isActive() }
                ?.toFollowingListItem(roomId, getFollowingInCircleCount(it.childRoomId))
        }
    }

    suspend fun removeRoomRelations(childRoomId: String) = createResult {
        roomRelationsBuilder.removeRelations(childRoomId, roomId)
    }

    suspend fun unfollowRoom(childRoomId: String) = createResult {
        roomRelationsBuilder.removeFromAllParents(childRoomId)
        session.roomService().leaveRoom(childRoomId)
    }

    private fun getFollowingInCircleCount(roomId: String): Int {
        var followingCount = 0
        session.roomService().getRoomSummaries(roomSummaryQueryParams { excludeType = null })
            .filter { summary ->
                summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN
            }.forEach { circle ->
                circle.spaceChildren?.firstOrNull { it.childRoomId == roomId }?.let {
                    followingCount++
                }
            }
        return followingCount
    }
}