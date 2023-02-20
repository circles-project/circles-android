package org.futo.circles.model

import android.net.Uri
import org.matrix.android.sdk.api.session.events.model.EventType

data class NotifiableMessageEvent(
    override val eventId: String,
    override val editedEventId: String?,
    override val canBeReplaced: Boolean,
    val noisy: Boolean,
    val timestamp: Long,
    val senderName: String?,
    val senderId: String?,
    val body: CharSequence?,
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
    override val isRedacted: Boolean = false,
    override val isUpdated: Boolean = false
) : NotifiableEvent {

    val type: String = EventType.MESSAGE
    val description: String = body.toString()
    val title: String = senderName ?: ""

    val imageUri: Uri?
        get() = imageUriString?.let { Uri.parse(it) }
}
