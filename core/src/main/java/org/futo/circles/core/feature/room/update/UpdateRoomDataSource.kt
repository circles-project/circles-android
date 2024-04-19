package org.futo.circles.core.feature.room.update

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getStateEvent
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import java.util.UUID
import javax.inject.Inject

@ViewModelScoped
class UpdateRoomDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getRoomSummary() = room?.roomSummary()

    fun getRoomPowerLevelFlow(): Flow<PowerLevelsContent> = room?.stateService()
        ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
        ?.asFlow()
        ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()

    suspend fun updateRoom(
        name: String,
        topic: String,
        uri: Uri?,
        isPublic: Boolean,
        userAccessLevel: AccessLevel?,
        isCircle: Boolean
    ) = createResult {
        if (isNameChanged(name)) room?.stateService()?.updateName(name)
        if (isTopicChanged(topic)) room?.stateService()?.updateTopic(topic)
        if (isPrivateSharedChanged(isPublic)) handelPrivateSharedVisibilityUpdate(isPublic)
        uri?.let { updateProfileImage(it, isCircle) }
        userAccessLevel?.let { updateUserDefaultPowerLevel(it) }
    }

    private suspend fun updateProfileImage(uri: Uri, isCircle: Boolean) {
        val roomToUpdate = if (isCircle) getTimelineRoomFor(roomId) else room
        roomToUpdate?.stateService()
            ?.updateAvatar(uri, uri.getFilename(context) ?: UUID.randomUUID().toString())
    }

    private suspend fun handelPrivateSharedVisibilityUpdate(isPublic: Boolean) {
        val timelineId = room?.roomId?.let { getTimelineRoomFor(it)?.roomId } ?: return
        if (isPublic) sharedCircleDataSource.addToSharedCircles(timelineId)
        else sharedCircleDataSource.removeFromSharedCircles(timelineId)
    }

    private suspend fun updateUserDefaultPowerLevel(accessLevel: AccessLevel) {
        val currentPowerLevel =
            room?.getStateEvent(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
                ?.content
                .toModel<PowerLevelsContent>() ?: return

        val newPowerLevelsContent = currentPowerLevel.copy(
            usersDefault = accessLevel.levelValue
        ).toContent()

        room?.stateService()
            ?.sendStateEvent(
                EventType.STATE_ROOM_POWER_LEVELS,
                stateKey = "",
                newPowerLevelsContent
            )
    }

    fun isNameChanged(newName: String) = room?.roomSummary()?.displayName != newName

    fun isTopicChanged(newTopic: String) = room?.roomSummary()?.topic != newTopic

    fun isPrivateSharedChanged(isPublic: Boolean) = room?.roomId?.let {
        isCircleShared(it) != isPublic
    } ?: false

    fun isCircleShared(circleId: String) = sharedCircleDataSource.isCircleShared(
        circleId,
        sharedCircleDataSource.getSharedCirclesTimelinesIds()
    )

}