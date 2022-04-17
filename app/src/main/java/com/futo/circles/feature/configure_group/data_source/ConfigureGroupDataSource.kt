package com.futo.circles.feature.configure_group.data_source

import android.content.Context
import android.net.Uri
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.getFilename
import com.futo.circles.provider.MatrixSessionProvider
import java.util.*

class ConfigureGroupDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    fun getRoomSummary() = room?.roomSummary()

    suspend fun updateGroup(name: String, topic: String, uri: Uri?) = createResult {
        if (isNameChanged(name)) room?.updateName(name)
        if (isTopicChanged(topic)) room?.updateTopic(topic)
        uri?.let {
            room?.updateAvatar(it, it.getFilename(context) ?: UUID.randomUUID().toString())
        }
    }

    fun isNameChanged(newName: String) = room?.roomSummary()?.displayName != newName

    fun isTopicChanged(newTopic: String) = room?.roomSummary()?.topic != newTopic

}