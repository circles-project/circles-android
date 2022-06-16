package org.futo.circles.feature.timeline.data_source

import android.content.Context
import android.net.Uri
import org.futo.circles.extensions.toImageContentAttachmentData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom

class SendMessageDataSource(private val context: Context) {

    private val session = MatrixSessionProvider.currentSession

    fun sendTextMessage(roomId: String, message: String, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId)
        threadEventId?.let { roomForMessage?.relationService()?.replyInThread(it, message) }
            ?: roomForMessage?.sendService()?.sendTextMessage(message)
    }

    fun sendImage(roomId: String, uri: Uri, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId)
        uri.toImageContentAttachmentData(context)?.let {
            roomForMessage?.sendService()?.sendMedia(it, true, emptySet(), threadEventId)
        }
    }

}