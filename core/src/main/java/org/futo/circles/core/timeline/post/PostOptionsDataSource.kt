package org.futo.circles.core.timeline.post

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.onBG
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.MediaShareable
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.model.TextShareable
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.core.utils.FileUtils.saveMediaFileToDevice
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import javax.inject.Inject

class PostOptionsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
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