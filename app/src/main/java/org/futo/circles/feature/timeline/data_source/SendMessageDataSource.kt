package org.futo.circles.feature.timeline.data_source

import android.content.Context
import android.net.Uri
import org.futo.circles.core.picker.MediaType
import org.futo.circles.extensions.toImageContentAttachmentData
import org.futo.circles.extensions.toVideoContentAttachmentData
import org.futo.circles.mapping.MediaCaptionFieldKey
import org.futo.circles.model.CreatePollContent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.content.ContentAttachmentData
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.RelationDefaultContent
import org.matrix.android.sdk.api.session.room.model.relation.ReplyToContent

class SendMessageDataSource(private val context: Context) {

    private val session = MatrixSessionProvider.currentSession

    fun sendTextMessage(roomId: String, message: String, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId) ?: return
        threadEventId?.let {
            sendTextReply(roomForMessage, threadEventId, message)
        } ?: roomForMessage.sendService().sendTextMessage(message, autoMarkdown = true)
    }

    private fun sendTextReply(roomForMessage: Room, threadEventId: String, message: String) {
        val event = roomForMessage.getTimelineEvent(threadEventId) ?: return
        roomForMessage.relationService().replyToMessage(event, message, autoMarkdown = true)
    }

    fun editTextMessage(eventId: String, roomId: String, message: String) {
        val roomForMessage = session?.getRoom(roomId) ?: return
        val event = roomForMessage.getTimelineEvent(eventId) ?: return
        roomForMessage.relationService()
            .editTextMessage(event, MessageType.MSGTYPE_TEXT, message, null, false)
    }

    fun sendMedia(
        roomId: String,
        uri: Uri,
        caption: String?,
        threadEventId: String?,
        type: MediaType
    ) {
        val roomForMessage = session?.getRoom(roomId) ?: return
        val content = when (type) {
            MediaType.Image -> uri.toImageContentAttachmentData(context)
            MediaType.Video -> uri.toVideoContentAttachmentData(context)
        } ?: return
        val shouldCompress = content.mimeType != WEBP_MIME_TYPE
        threadEventId?.let {
            sendMediaReply(roomForMessage, content, shouldCompress, it)
        } ?: roomForMessage.sendService().sendMedia(
            content,
            shouldCompress,
            emptySet(),
            additionalContent = caption?.let { mapOf(MediaCaptionFieldKey to it) }
        )
    }

    private fun sendMediaReply(
        roomForMessage: Room,
        content: ContentAttachmentData,
        shouldCompress: Boolean,
        threadEventId: String
    ) {
        val replyToContent = RelationDefaultContent(null, null, ReplyToContent(threadEventId))
        roomForMessage.sendService()
            .sendMedia(content, shouldCompress, emptySet(), null, replyToContent)
    }

    fun createPoll(roomId: String, pollContent: CreatePollContent) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.sendService()
            ?.sendPoll(pollContent.pollType, pollContent.question, pollContent.options)
    }

    fun editPoll(roomId: String, eventId: String, pollContent: CreatePollContent) {
        val roomForMessage = session?.getRoom(roomId) ?: return
        val event = roomForMessage.getTimelineEvent(eventId) ?: return
        roomForMessage.relationService()
            .editPoll(event, pollContent.pollType, pollContent.question, pollContent.options)
    }

    companion object {
        private const val WEBP_MIME_TYPE = "image/webp"
    }
}