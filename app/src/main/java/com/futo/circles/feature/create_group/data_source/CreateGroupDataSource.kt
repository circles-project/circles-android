package com.futo.circles.feature.create_group.data_source

import android.net.Uri
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider
import com.futo.circles.utils.GROUP_TAG
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset

class CreateGroupDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun createGroup(
        iconUri: Uri?,
        name: String,
        topic: String
    ) = createResult {
        val groupId =
            session?.createRoom(getCreateRoomParams(iconUri, name, topic))

        groupId?.let { addGroupTag(it) }
    }

    private fun getCreateRoomParams(
        iconUri: Uri?,
        groupName: String,
        groupTopic: String
    ) = CreateRoomParams().apply {
        name = groupName
        topic = groupTopic
        avatarUri = iconUri
        visibility = RoomDirectoryVisibility.PRIVATE
        preset = CreateRoomPreset.PRESET_PRIVATE_CHAT
    }

    private suspend fun addGroupTag(groupId: String) {
        session?.getRoom(groupId)?.addTag(GROUP_TAG, null)
    }

}
