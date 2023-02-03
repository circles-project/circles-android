package org.futo.circles.feature.notifications

import android.content.Context
import android.net.Uri
import org.futo.circles.R
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.*
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import org.matrix.android.sdk.api.session.crypto.model.OlmDecryptionResult
import org.matrix.android.sdk.api.session.events.model.*
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.message.MessageWithAttachmentContent
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getEditedEventId
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import java.util.*

inline fun <reified R> Any?.takeAs(): R? {
    return takeIf { it is R } as R?
}

class NotifiableEventResolver(
    private val context: Context,
    private val displayableEventFormatter: DisplayableEventFormatter
) {

    private val nonEncryptedNotifiableEventTypes: List<String> =
        listOf(EventType.MESSAGE) + EventType.POLL_START.values

    suspend fun resolveEvent(event: Event, session: Session, isNoisy: Boolean): NotifiableMessageEvent? {
        val roomID = event.roomId ?: return null
        val eventId = event.eventId ?: return null
        val timelineEvent = session.getRoom(roomID)?.getTimelineEvent(eventId) ?: return null
        return when (event.getClearType()) {
            in nonEncryptedNotifiableEventTypes,
            EventType.ENCRYPTED -> {
                resolveMessageEvent(timelineEvent, session, canBeReplaced = false)
            }
            else -> null
        }
    }

    suspend fun resolveInMemoryEvent(
        session: Session,
        event: Event,
        canBeReplaced: Boolean
    ): NotifiableMessageEvent? {
        if (!event.supportsNotification()) return null

        if (event.isEdition()) return null

        val actions = session.pushRuleService().getActions(event)
        val notificationAction = actions.toNotificationAction()

        return if (notificationAction.shouldNotify) {
            val user = session.getUserOrDefault(event.senderId!!)

            val timelineEvent = TimelineEvent(
                root = event,
                localId = -1,
                eventId = event.eventId!!,
                displayIndex = 0,
                senderInfo = SenderInfo(
                    userId = user.userId,
                    displayName = user.notEmptyDisplayName(),
                    isUniqueDisplayName = true,
                    avatarUrl = user.avatarUrl
                )
            )
            resolveMessageEvent(
                timelineEvent,
                session,
                canBeReplaced = canBeReplaced
            )
        } else null
    }

    private suspend fun resolveMessageEvent(
        event: TimelineEvent,
        session: Session,
        canBeReplaced: Boolean
    ): NotifiableMessageEvent? {
        val room = session.getRoom(event.root.roomId!!)
        return if (room == null) {
            val body = displayableEventFormatter.format(event, appendAuthor = false)
            val roomName = context.getString(R.string.notification_unknown_room_name)
            val senderDisplayName = event.senderInfo.disambiguatedDisplayName

            NotifiableMessageEvent(
                eventId = event.root.eventId!!,
                editedEventId = event.getEditedEventId(),
                canBeReplaced = canBeReplaced,
                timestamp = event.root.originServerTs ?: 0,
                noisy = true,
                senderName = senderDisplayName,
                senderId = event.root.senderId,
                body = body.toString(),
                imageUriString = event.fetchImageIfPresent(session)?.toString(),
                roomId = event.root.roomId!!,
                threadId = event.root.getRootThreadEventId(),
                roomName = roomName,
                matrixID = session.myUserId
            )
        } else {
            event.attemptToDecryptIfNeeded(session)
            when (event.root.getClearType()) {
                in nonEncryptedNotifiableEventTypes -> {
                    val body = displayableEventFormatter.format(
                        event,
                        appendAuthor = false
                    ).toString()
                    val roomName = room.roomSummary()?.displayName ?: ""
                    val senderDisplayName = event.senderInfo.disambiguatedDisplayName

                    NotifiableMessageEvent(
                        eventId = event.root.eventId!!,
                        editedEventId = event.getEditedEventId(),
                        canBeReplaced = canBeReplaced,
                        timestamp = event.root.originServerTs ?: 0,
                        noisy = true,
                        senderName = senderDisplayName,
                        senderId = event.root.senderId,
                        body = body,
                        imageUriString = event.fetchImageIfPresent(session)?.toString(),
                        roomId = event.root.roomId!!,
                        threadId = event.root.getRootThreadEventId(),
                        roomName = roomName,
                        roomIsDirect = room.roomSummary()?.isDirect ?: false,
                        roomAvatarPath = session.contentUrlResolver()
                            .resolveThumbnail(
                                room.roomSummary()?.avatarUrl,
                                250,
                                250,
                                ContentUrlResolver.ThumbnailMethod.SCALE
                            ),
                        senderAvatarPath = session.contentUrlResolver()
                            .resolveThumbnail(
                                event.senderInfo.avatarUrl,
                                250,
                                250,
                                ContentUrlResolver.ThumbnailMethod.SCALE
                            ),
                        matrixID = session.myUserId,
                        soundName = null
                    )
                }
                else -> null
            }
        }
    }

    private suspend fun TimelineEvent.attemptToDecryptIfNeeded(session: Session) {
        if (root.isEncrypted() && root.mxDecryptionResult == null) {
            try {
                val result = session.cryptoService()
                    .decryptEvent(root, root.roomId + UUID.randomUUID().toString())
                root.mxDecryptionResult = OlmDecryptionResult(
                    payload = result.clearEvent,
                    senderKey = result.senderCurve25519Key,
                    keysClaimed = result.claimedEd25519Key?.let { mapOf("ed25519" to it) },
                    forwardingCurve25519KeyChain = result.forwardingCurve25519KeyChain,
                    isSafe = result.isSafe
                )
            } catch (ignore: MXCryptoError) {
            }
        }
    }

    private suspend fun TimelineEvent.fetchImageIfPresent(session: Session): Uri? {
        return when {
            root.isEncrypted() && root.mxDecryptionResult == null -> null
            root.isImageMessage() -> downloadAndExportImage(session)
            else -> null
        }
    }

    private suspend fun TimelineEvent.downloadAndExportImage(session: Session): Uri? {
        return kotlin.runCatching {
            getLastMessageContent()?.takeAs<MessageWithAttachmentContent>()
                ?.let { imageMessage ->
                    val fileService = session.fileService()
                    fileService.downloadFile(imageMessage)
                    fileService.getTemporarySharableURI(imageMessage)
                }
        }.getOrNull()
    }

}
