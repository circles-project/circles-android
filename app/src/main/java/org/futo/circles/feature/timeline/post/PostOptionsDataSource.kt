package org.futo.circles.feature.timeline.post

import android.content.Context
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.core.utils.FileUtils.saveMediaFileToDevice
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.onBG
import org.futo.circles.feature.share.MediaShareable
import org.futo.circles.feature.share.ShareableContent
import org.futo.circles.feature.share.TextShareable
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class PostOptionsDataSource(
    private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession

    fun removeMessage(roomId: String, eventId: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.getTimelineEvent(eventId)
            ?.let { roomForMessage.sendService().redactEvent(it.root, null) }
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.sendReaction(eventId, emoji)
    }

    suspend fun unSendReaction(roomId: String, eventId: String, emoji: String) = createResult {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.undoReaction(eventId, emoji)
    }


    suspend fun getShareableContent(content: PostContent): ShareableContent? = onBG {
        when (content) {
            is ImageContent -> getShareableMediaContent(content.mediaContentData)
            is VideoContent -> getShareableMediaContent(content.mediaContentData)
            is TextContent -> TextShareable(content.message)
            else -> throw IllegalArgumentException("Not shareable post content")
        }
    }

    suspend fun saveMediaToDevice(content: PostContent) = onBG {
        when (content) {
            is ImageContent -> saveMediaFileToDevice(
                context, content.mediaContentData, MediaType.Image
            )
            is VideoContent -> saveMediaFileToDevice(
                context, content.mediaContentData, MediaType.Video
            )
            else -> throw IllegalArgumentException("Unsupported file type")
        }
    }

    private suspend fun getShareableMediaContent(mediaContentData: MediaContentData) =
        downloadEncryptedFileToContentUri(context, mediaContentData)?.let {
            MediaShareable(it, mediaContentData.mimeType)
        }
}