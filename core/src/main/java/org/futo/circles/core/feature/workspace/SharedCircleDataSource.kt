package org.futo.circles.core.feature.workspace

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.model.PROFILE_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class SharedCircleDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    fun getSharedCirclesSpaceId() =
        spacesTreeAccountDataSource.getRoomIdByKey(PROFILE_SPACE_ACCOUNT_DATA_KEY)

    suspend fun unfollowUsersSharedCircle(userId: String) {
        val userSharedCircleId =
            getSharedCircleFor(userId)?.roomId
                ?: throw IllegalArgumentException("User's profile not found")
        val mySharedCircleId = getSharedCirclesSpaceId()
            ?: throw IllegalArgumentException("Workspace configuration failure")

        roomRelationsBuilder.removeRelations(userSharedCircleId, mySharedCircleId)
        MatrixSessionProvider.getSessionOrThrow().roomService().leaveRoom(userSharedCircleId)
    }

    suspend fun addToSharedCircles(timelineId: String) {
        getSharedCirclesSpaceId()?.let { roomRelationsBuilder.setRelations(timelineId, it) }
    }

    suspend fun removeFromSharedCircles(timelineId: String) {
        getSharedCirclesSpaceId()?.let { roomRelationsBuilder.removeRelations(timelineId, it) }
    }

    fun getSharedCircleFor(userId: String): RoomSummary? {
        val sharedCirclesSpaceId = getSharedCirclesSpaceId() ?: return null
        val userSharedCircleId =
            getJoinedRoomById(sharedCirclesSpaceId)?.roomSummary()?.spaceChildren?.firstOrNull { child ->
                getRoomOwner(child.childRoomId)?.userId == userId
            }?.childRoomId ?: return null
        return getJoinedRoomById(userSharedCircleId)?.roomSummary()
    }

    fun getSharedCirclesTimelinesIds() = getSharedCirclesSpaceId()?.let {
        MatrixSessionProvider.currentSession?.getRoomSummary(it)?.spaceChildren?.map { it.childRoomId }
    } ?: emptyList()

    fun isCircleShared(circleId: String, sharedCirclesTimelineIds: List<String>): Boolean {
        val timelineId = getTimelineRoomFor(circleId)?.roomId
        return sharedCirclesTimelineIds.contains(timelineId)
    }

    suspend fun acceptSharedCircleInvite(roomId: String) = createResult {
        MatrixSessionProvider.getSessionOrThrow().roomService().joinRoom(roomId)
        val sharedCirclesSpaceId =
            getSharedCirclesSpaceId() ?: throw IllegalArgumentException("Profile space not found")
        roomRelationsBuilder.setRelations(roomId, sharedCirclesSpaceId)
    }
}