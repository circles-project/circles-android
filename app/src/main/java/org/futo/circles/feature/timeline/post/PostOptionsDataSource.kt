package org.futo.circles.feature.timeline.post

import android.content.Context
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.onBG
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.core.utils.FileUtils.saveMediaFileToDevice
import org.futo.circles.feature.share.MediaShareable
import org.futo.circles.feature.share.ShareableContent
import org.futo.circles.feature.share.TextShareable
import org.futo.circles.model.MediaContent
import org.futo.circles.model.PostContent
import org.futo.circles.model.TextContent
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
            is MediaContent -> getShareableMediaContent(content.mediaFileData)
            is TextContent -> TextShareable(content.message)
            else -> throw IllegalArgumentException("Not shareable post content")
        }
    }

    suspend fun saveMediaToDevice(content: PostContent) {
        val mediaContent = content as? MediaContent ?: return
        onBG {
            saveMediaFileToDevice(context, mediaContent.mediaFileData, mediaContent.getMediaType())
        }
    }

    private suspend fun getShareableMediaContent(mediaFileData: MediaFileData) =
        downloadEncryptedFileToContentUri(context, mediaFileData)?.let {
            MediaShareable(it, mediaFileData.mimeType)
        }

    fun pollVote(roomId: String, eventId: String, pollOptionId: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.sendService()?.voteToPoll(eventId, pollOptionId)
    }

    fun endPoll(roomId: String, eventId: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.sendService()?.endPoll(eventId)
    }
}