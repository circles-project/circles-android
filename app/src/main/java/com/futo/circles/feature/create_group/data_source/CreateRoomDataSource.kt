package com.futo.circles.feature.create_group.data_source

import android.net.Uri
import com.futo.circles.core.GROUP_TYPE
import com.futo.circles.core.SPACE_TYPE
import com.futo.circles.model.UserListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset
import org.matrix.android.sdk.api.session.space.CreateSpaceParams

class CreateRoomDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun createGroup(
        iconUri: Uri?,
        name: String,
        topic: String,
        usersToInvite: List<UserListItem>
    ) {
        val inviteIds = usersToInvite.map { it.id }
        val params = CreateRoomParams().apply {
            this.name = name
            this.topic = topic
            avatarUri = iconUri
            invitedUserIds.addAll(inviteIds)
            visibility = RoomDirectoryVisibility.PRIVATE
            preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
            roomType = GROUP_TYPE
        }
        session?.createRoom(params)
    }

    suspend fun createSpace(name: String, iconUri: Uri? = null) {
        val params = CreateSpaceParams().apply {
            this.name = name
            avatarUri = iconUri
            roomType = SPACE_TYPE
        }
        session?.spaceService()?.createSpace(params)
    }
}
