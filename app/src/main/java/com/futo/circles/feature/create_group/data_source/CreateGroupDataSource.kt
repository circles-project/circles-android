package com.futo.circles.feature.create_group.data_source

import android.net.Uri
import com.futo.circles.extensions.createResult
import com.futo.circles.model.UserListItem
import com.futo.circles.provider.MatrixSessionProvider
import com.futo.circles.core.GROUP_TAG
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset

class CreateGroupDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun createGroup(
        iconUri: Uri?,
        name: String,
        topic: String,
        usersToInvite: List<UserListItem>
    ) = createResult {
        val inviteIds = usersToInvite.map { it.id }

        val groupId =
            session?.createRoom(getCreateRoomParams(iconUri, name, topic, inviteIds))

        groupId?.let { addGroupTag(it) }
    }

    private fun getCreateRoomParams(
        iconUri: Uri?,
        groupName: String,
        groupTopic: String,
        inviteIds: List<String>
    ) = CreateRoomParams().apply {
        name = groupName
        topic = groupTopic
        avatarUri = iconUri
        invitedUserIds.addAll(inviteIds)
        visibility = RoomDirectoryVisibility.PRIVATE
        preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
    }

    private suspend fun addGroupTag(groupId: String) {
        session?.getRoom(groupId)?.addTag(GROUP_TAG, null)
    }

}
