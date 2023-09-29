package org.futo.circles.core.workspace

import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.model.PROFILE_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class SharedCircleDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    fun getSharedCirclesSpaceId() =
        spacesTreeAccountDataSource.getRoomIdByKey(PROFILE_SPACE_ACCOUNT_DATA_KEY)

    suspend fun addToSharedCircles(timelineId: String) {
        getSharedCirclesSpaceId()?.let { roomRelationsBuilder.setRelations(timelineId, it) }
    }

    suspend fun removeFromSharedCircles(timelineId: String) {
        getSharedCirclesSpaceId()?.let { roomRelationsBuilder.removeRelations(timelineId, it) }
    }

    fun getSharedCircleFor(userId: String) = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })?.firstOrNull { summary ->
            summary.roomType == RoomType.SPACE && summary.membership == Membership.JOIN &&
                    getRoomOwners(summary.roomId).map { it.userId }.contains(userId)
        }

    fun getSharedCirclesTimelinesIds() = getSharedCirclesSpaceId()?.let {
        MatrixSessionProvider.currentSession?.getRoomSummary(it)?.spaceChildren?.map { it.childRoomId }
    } ?: emptyList()

    fun isCircleShared(circleId: String, sharedCirclesTimelineIds: List<String>): Boolean {
        val timelineId = getTimelineRoomFor(circleId)?.roomId
        return sharedCirclesTimelineIds.contains(timelineId)
    }
}