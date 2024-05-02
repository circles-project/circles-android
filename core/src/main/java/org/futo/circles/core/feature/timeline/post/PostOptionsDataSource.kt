package org.futo.circles.core.feature.timeline.post

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getPostContentType
import org.futo.circles.core.extensions.getUri
import org.futo.circles.core.extensions.onBG
import org.futo.circles.core.mapping.toMediaContent
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaFileData
import org.futo.circles.core.model.MediaShareable
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType.IMAGE_CONTENT
import org.futo.circles.core.model.PostContentType.VIDEO_CONTENT
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.model.TextShareable
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.FileUtils.createImageFile
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.core.utils.FileUtils.saveMediaFileToDevice
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.util.MimeTypes
import java.io.FileOutputStream
import javax.inject.Inject


class PostOptionsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession

    suspend fun removeMessage(roomId: String, eventId: String) {
        val roomForMessage = session?.getRoom(roomId) ?: return
        val event = roomForMessage.getTimelineEvent(eventId) ?: return
        roomForMessage.sendService().redactEvent(event.root, null)

        val eventType = event.getPostContentType()
        val mediaEvent = when (eventType) {
            IMAGE_CONTENT -> event.toMediaContent(MediaType.Image)
            VIDEO_CONTENT -> event.toMediaContent(MediaType.Video)
            else -> return
        }
        tryOrNull { session.mediaService().deleteMediaFile(mediaEvent.mediaFileData.fileUrl) }
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.sendReaction(eventId, emoji)
    }

    suspend fun unSendReaction(roomId: String, eventId: String, emoji: String) = createResult {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.undoReaction(eventId, emoji)
    }


    suspend fun getShareableContent(content: PostContent, view: View? = null): ShareableContent? =
        onBG {
            when (content) {
                is MediaContent -> getShareableMediaContent(content.mediaFileData)
                is TextContent -> TextShareable(content.messageSpanned.toString())
                is PollContent -> view?.let { getShareableMediaView(it) }
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

    private suspend fun getShareableMediaView(v: View): MediaShareable? =
        withContext(Dispatchers.IO) {
            tryOrNull {
                val bitmap = Bitmap.createBitmap(
                    v.width,
                    v.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                v.draw(canvas)
                canvas.drawColor(Color.WHITE, PorterDuff.Mode.DST_ATOP)

                val file = createImageFile(context)
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                stream.close()
                bitmap.recycle()

                MediaShareable(file.getUri(context), MimeTypes.Images)
            }
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