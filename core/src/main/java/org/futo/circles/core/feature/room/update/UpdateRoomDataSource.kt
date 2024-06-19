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
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getStateEvent
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.internal.session.content.ThumbnailExtractor
import java.util.UUID
import javax.inject.Inject

@ViewModelScoped
class UpdateRoomDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
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
        userAccessLevel: AccessLevel?,
        roomTypeArg: CircleRoomTypeArg
    ) = createResult {
        if (isNameChanged(name)) updateName(name, roomTypeArg)
        if (isTopicChanged(topic)) room?.stateService()?.updateTopic(topic)
        uri?.let { updateProfileImage(it, roomTypeArg) }
        userAccessLevel?.let { updateUserDefaultPowerLevel(it) }
    }

    private suspend fun updateName(name: String, roomTypeArg: CircleRoomTypeArg) {
        if (roomTypeArg == CircleRoomTypeArg.Circle) {
            getTimelineRoomFor(roomId)?.stateService()?.updateName(name)
        } else room?.stateService()?.updateName(name)
    }

    private suspend fun updateProfileImage(uri: Uri, roomTypeArg: CircleRoomTypeArg) {
        val isCircle = roomTypeArg == CircleRoomTypeArg.Circle
        val isGallery = roomTypeArg == CircleRoomTypeArg.Photo
        val roomToUpdate = if (isCircle) getTimelineRoomFor(roomId) else room
        roomToUpdate?.stateService()?.updateAvatar(
            uri,
            uri.getFilename(context) ?: UUID.randomUUID().toString(),
            if (isGallery) ThumbnailExtractor.POST_THUMB_SIZE else ThumbnailExtractor.PROFILE_ICON_THUMB_SIZE
        )
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

}