package org.futo.circles.feature.room.update

import android.content.Context
import android.net.Uri
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.getFilename
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.GuestAccess
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import java.util.*

class UpdateRoomDataSource(
    roomId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getRoomSummary() = room?.roomSummary()

    suspend fun updateRoom(name: String, topic: String, uri: Uri?, joinRules: RoomJoinRules) =
        createResult {
            if (isNameChanged(name)) room?.stateService()?.updateName(name)
            if (isTopicChanged(topic)) room?.stateService()?.updateTopic(topic)
            if (isJoinRulesChanged(joinRules)) room?.stateService()
                ?.updateJoinRule(joinRules, GuestAccess.CanJoin)
            uri?.let {
                room?.stateService()
                    ?.updateAvatar(it, it.getFilename(context) ?: UUID.randomUUID().toString())
            }
        }

    fun isNameChanged(newName: String) = room?.roomSummary()?.displayName != newName

    fun isTopicChanged(newTopic: String) = room?.roomSummary()?.topic != newTopic

    fun isJoinRulesChanged(joinRules: RoomJoinRules) = room?.roomSummary()?.joinRules != joinRules

}