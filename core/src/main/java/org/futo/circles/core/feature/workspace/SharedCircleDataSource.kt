package org.futo.circles.core.feature.workspace

import android.os.Build
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getRoomOwners
import org.futo.circles.core.feature.room.RoomRelationsBuilder
import org.futo.circles.core.model.PROFILE_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
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

    fun observeAndAutoAcceptSharedSpaceInvites(coroutineScope: CoroutineScope): Job {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return Job()
        val session = MatrixSessionProvider.currentSession ?: return Job()
        return session.roomService().getRoomSummariesLive(
            roomSummaryQueryParams {
                excludeType = null
                memberships = listOf(Membership.INVITE)
            }).map { it.filter { it.roomType == RoomType.SPACE }.map { it.roomId } }
            .asFlow().onEach { roomsIds ->
                withContext(Dispatchers.IO) {
                    roomsIds.forEach { acceptSharedCircleInvite(session, it) }
                }
            }
            .flowOn(Dispatchers.Default)
            .catch { }
            .launchIn(coroutineScope)
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
                getRoomOwners(child.childRoomId).map { it.userId }.contains(userId)
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

    private suspend fun acceptSharedCircleInvite(session: Session, roomId: String) {
        session.roomService().joinRoom(roomId)
        val sharedCirclesSpaceId = getSharedCirclesSpaceId() ?: return
        roomRelationsBuilder.setRelations(roomId, sharedCirclesSpaceId)
    }
}