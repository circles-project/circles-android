package com.futo.circles.core.matrix.room

import android.content.Context
import android.net.Uri
import com.futo.circles.model.Circle
import com.futo.circles.model.CirclesRoom
import com.futo.circles.model.Timeline
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.space.CreateSpaceParams

class CreateRoomDataSource(
    private val context: Context,
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun createCircleWithTimeline(
        name: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): String {
        val circleId = createRoom(Circle(), name, null, iconUri)
        val timelineId = createRoom(Timeline(), name, null, iconUri, inviteIds)
        session?.getRoom(circleId)
            ?.let { circle -> roomRelationsBuilder.setRelations(timelineId, circle) }
        return circleId
    }

    suspend fun createRoom(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): String {
        val id = session?.roomService()
            ?.createRoom(getParams(circlesRoom, name, topic, iconUri, inviteIds))
            ?: throw Exception("Can not create room")

        session?.getRoom(id)?.tagsService()?.addTag(circlesRoom.tag, null)
        circlesRoom.parentTag?.let { tag ->
            roomRelationsBuilder.findRoomByTag(tag)
                ?.let { room -> roomRelationsBuilder.setRelations(id, room) }
        }
        return id
    }

    private fun getParams(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): CreateRoomParams {
        val params = if (circlesRoom.isSpace()) {
            CreateSpaceParams()
        } else {
            CreateRoomParams().apply {
                visibility = RoomDirectoryVisibility.PRIVATE
                preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
                powerLevelContentOverride = PowerLevelsContent(
                    invite = Role.Moderator.value
                )
            }
        }.apply {
            circlesRoom.type?.let { this.roomType = it }
        }

        return params.apply {
            this.name = circlesRoom.nameId?.let { context.getString(it) } ?: name
            this.topic = topic
            avatarUri = iconUri
            inviteIds?.let { invitedUserIds.addAll(it) }
        }
    }
}
