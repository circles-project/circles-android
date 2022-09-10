package org.futo.circles.feature.timeline.data_source

import android.content.Context
import android.net.Uri
import org.futo.circles.core.picker.MediaType
import org.futo.circles.extensions.toImageContentAttachmentData
import org.futo.circles.extensions.toVideoContentAttachmentData
import org.futo.circles.model.CreatePollContent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

class SendMessageDataSource(private val context: Context) {

    private val session = MatrixSessionProvider.currentSession

    fun sendTextMessage(roomId: String, message: String, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId)
        threadEventId?.let { roomForMessage?.relationService()?.replyInThread(it, message) }
            ?: roomForMessage?.sendService()?.sendTextMessage(message)
    }

    fun sendMedia(roomId: String, uri: Uri, threadEventId: String?, type: MediaType) {
        val roomForMessage = session?.getRoom(roomId)
        val content = when (type) {
            MediaType.Image -> uri.toImageContentAttachmentData(context)
            MediaType.Video -> uri.toVideoContentAttachmentData(context)
        }
        content?.let {
            roomForMessage?.sendService()?.sendMedia(it, true, emptySet(), threadEventId)
        }
    }

    fun createPoll(roomId: String, pollContent: CreatePollContent) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.sendService()
            ?.sendPoll(pollContent.pollType, pollContent.question, pollContent.options)

    }

}