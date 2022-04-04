package com.futo.circles.feature.create_group.data_source

import android.net.Uri
import com.futo.circles.core.GROUPS_SPACE_TYPE
import com.futo.circles.core.GROUP_TYPE
import com.futo.circles.core.matrix.CreateRoomDataSource
import com.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset

class CreateGroupDataSource : CreateRoomDataSource() {

    suspend fun createGroup(
        iconUri: Uri?,
        name: String,
        topic: String,
        usersToInvite: List<UserListItem>
    ): String {
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
        val groupId = session?.createRoom(params) ?: ""
        setRelations(groupId, GROUPS_SPACE_TYPE)
        return groupId
    }
}