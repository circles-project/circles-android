package org.futo.circles.feature.timeline.post

import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.onBG
import org.futo.circles.extensions.saveImageToDeviceGallery
import org.futo.circles.feature.timeline.post.share.MediaShareable
import org.futo.circles.feature.timeline.post.share.ShareableContent
import org.futo.circles.feature.timeline.post.share.TextShareable
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
        }
    }

    suspend fun saveMediaToDevice(content: PostContent) = withContext(Dispatchers.IO) {
        when (content) {
            is ImageContent -> Glide.with(context).asBitmap().load(content.mediaContentData)
                .submit().get()
                .saveImageToDeviceGallery(context)
            is VideoContent -> TODO()
            else -> {}
        }
    }

    private suspend fun getShareableMediaContent(mediaContentData: MediaContentData) =
        downloadEncryptedFileToContentUri(context, mediaContentData)?.let {
            MediaShareable(it, mediaContentData.mimeType)
        }
}