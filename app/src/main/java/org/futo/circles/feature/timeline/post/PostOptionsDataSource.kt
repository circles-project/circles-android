package org.futo.circles.feature.timeline.post

import android.content.Context
import com.bumptech.glide.Glide
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.getUri
import org.futo.circles.extensions.saveImageToDeviceGallery
import org.futo.circles.feature.timeline.post.share.ImageShareable
import org.futo.circles.feature.timeline.post.share.TextShareable
import org.futo.circles.model.ImageContent
import org.futo.circles.model.PostContent
import org.futo.circles.model.TextContent
import org.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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


    suspend fun getShareableContent(content: PostContent) = withContext(Dispatchers.IO) {
        when (content) {
            is ImageContent -> {
                val uri = Glide.with(context).asFile().load(content).submit().get().getUri(context)
                ImageShareable(uri)
            }
            is TextContent -> TextShareable(content.message)
        }
    }

    suspend fun saveImageToDevice(imageContent: ImageContent) = withContext(Dispatchers.IO) {
        val b = Glide.with(context).asBitmap().load(imageContent).submit().get()
        b.saveImageToDeviceGallery(context)
    }
}