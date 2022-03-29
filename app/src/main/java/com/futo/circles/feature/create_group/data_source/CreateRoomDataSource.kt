package com.futo.circles.feature.create_group.data_source

import android.net.Uri
import com.futo.circles.core.CIRCLE_TYPE
import com.futo.circles.core.GROUP_TYPE
import com.futo.circles.model.UserListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset

class CreateRoomDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun createGroup(
        iconUri: Uri?,
        name: String,
        topic: String,
        usersToInvite: List<UserListItem>
    ) {
        val inviteIds = usersToInvite.map { it.id }
        session?.createRoom(getCreateRoomParams(GROUP_TYPE, name, iconUri, topic, inviteIds))
    }

    suspend fun createCircle(iconUri: Uri?, name: String) {
        session?.createRoom(getCreateRoomParams(CIRCLE_TYPE, name, iconUri))
    }

    private fun getCreateRoomParams(
        type: String,
        roomName: String,
        iconUri: Uri?,
        roomTopic: String? = null,
        inviteIds: List<String> = emptyList()
    ) = CreateRoomParams().apply {
        name = roomName
        topic = roomTopic
        avatarUri = iconUri
        invitedUserIds.addAll(inviteIds)
        visibility = RoomDirectoryVisibility.PRIVATE
        preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
        roomType = type
    }
}
