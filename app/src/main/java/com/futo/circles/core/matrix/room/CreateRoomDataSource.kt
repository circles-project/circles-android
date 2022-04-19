package com.futo.circles.core.matrix.room

import android.content.Context
import android.net.Uri
import com.futo.circles.BuildConfig
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import org.matrix.android.sdk.api.session.space.CreateSpaceParams

class CreateRoomDataSource(private val context: Context) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun createCirclesRoom(
        circlesRoom: CirclesRoom,
        name: String? = null,
        topic: String? = null,
        iconUri: Uri? = null,
        inviteIds: List<String>? = null
    ): String {

        val id = session?.createRoom(getParams(circlesRoom, name, topic, iconUri, inviteIds))
            ?: throw Exception("Can not create room")

        session?.getRoom(id)?.addTag(circlesRoom.tag, null)
        circlesRoom.parentTag?.let { setRelations(id, it) }

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

    private suspend fun setRelations(childId: String, parentTag: String) {
        val parentRoom = findRoomByTag(parentTag) ?: return
        val via = listOf(getHomeServerDomain())
        session?.spaceService()?.setSpaceParent(childId, parentRoom.roomId, true, via)
        parentRoom.asSpace()?.addChildren(childId, via, null)
    }

    private fun findRoomByTag(tag: String): Room? {
        val roomWithTagId = session?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
            ?.firstOrNull { summary -> summary.tags.firstOrNull { it.name == tag } != null }
            ?.roomId
        return roomWithTagId?.let { session?.getRoom(it) }
    }

    private fun getHomeServerDomain() = BuildConfig.MATRIX_HOME_SERVER_URL
        .substringAfter("//").replace("/", "")
}
