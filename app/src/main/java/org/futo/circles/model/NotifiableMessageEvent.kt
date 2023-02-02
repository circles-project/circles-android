package org.futo.circles.model

import android.net.Uri
import org.matrix.android.sdk.api.session.events.model.EventType
import java.io.Serializable

data class NotifiableMessageEvent(
        val eventId: String,
        val editedEventId: String?,
        val canBeReplaced: Boolean,
        val noisy: Boolean,
        val timestamp: Long,
        val senderName: String?,
        val senderId: String?,
        val body: String?,
        val imageUriString: String?,
        val roomId: String,
        val threadId: String?,
        val roomName: String?,
        val roomIsDirect: Boolean = false,
        val roomAvatarPath: String? = null,
        val senderAvatarPath: String? = null,
        val matrixID: String? = null,
        val soundName: String? = null,
        val outGoingMessage: Boolean = false,
        val outGoingMessageFailed: Boolean = false,
        val isRedacted: Boolean = false,
        val isUpdated: Boolean = false
) : Serializable {

    val type: String = EventType.MESSAGE
    val description: String = body ?: ""
    val title: String = senderName ?: ""

    val imageUri: Uri?
        get() = imageUriString?.let { Uri.parse(it) }
}
