package com.futo.circles.feature.room.update_room

import android.content.Context
import android.net.Uri
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.getFilename
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import java.util.*

class UpdateRoomDataSource(
    roomId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getRoomSummary() = room?.roomSummary()

    suspend fun updateRoom(name: String, topic: String, uri: Uri?) = createResult {
        if (isNameChanged(name)) room?.stateService()?.updateName(name)
        if (isTopicChanged(topic)) room?.stateService()?.updateTopic(topic)
        uri?.let {
            room?.stateService()
                ?.updateAvatar(it, it.getFilename(context) ?: UUID.randomUUID().toString())
        }
    }

    fun isNameChanged(newName: String) = room?.roomSummary()?.displayName != newName

    fun isTopicChanged(newTopic: String) = room?.roomSummary()?.topic != newTopic

}