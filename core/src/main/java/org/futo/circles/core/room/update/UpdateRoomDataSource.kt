package org.futo.circles.core.room.update

import android.content.Context
import android.net.Uri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.CreateRoomDataSource
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.core.utils.isCircleShared
import org.matrix.android.sdk.api.session.getRoom
import java.util.UUID

class UpdateRoomDataSource @AssistedInject constructor(
    @Assisted roomId: String,
    @ApplicationContext private val context: Context,
    private val createRoomDataSource: CreateRoomDataSource
) {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): UpdateRoomDataSource
    }

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getRoomSummary() = room?.roomSummary()

    suspend fun updateRoom(name: String, topic: String, uri: Uri?, isPublic: Boolean) =
        createResult {
            if (isNameChanged(name)) room?.stateService()?.updateName(name)
            if (isTopicChanged(topic)) room?.stateService()?.updateTopic(topic)
            if (isPrivateSharedChanged(isPublic)) handelPrivateSharedVisibilityUpdate(isPublic)
            uri?.let {
                room?.stateService()
                    ?.updateAvatar(it, it.getFilename(context) ?: UUID.randomUUID().toString())
            }
        }

    private suspend fun handelPrivateSharedVisibilityUpdate(isPublic: Boolean) {
        val timelineId = room?.roomId?.let { getTimelineRoomFor(it)?.roomId } ?: return
        if (isPublic) createRoomDataSource.addToSharedCircles(timelineId)
        else createRoomDataSource.removeFromSharedCircles(timelineId)
    }

    fun isNameChanged(newName: String) = room?.roomSummary()?.displayName != newName

    fun isTopicChanged(newTopic: String) = room?.roomSummary()?.topic != newTopic

    fun isPrivateSharedChanged(isPublic: Boolean) = room?.roomId?.let {
        isCircleShared(it) != isPublic
    } ?: false

}