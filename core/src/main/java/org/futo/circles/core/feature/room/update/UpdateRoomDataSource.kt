package org.futo.circles.core.feature.room.update

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.matrix.android.sdk.api.session.getRoom
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
        if (isPublic) sharedCircleDataSource.addToSharedCircles(timelineId)
        else sharedCircleDataSource.removeFromSharedCircles(timelineId)
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