package org.futo.circles.core.feature.notifications

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.R
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.RoomThirdPartyInviteContent
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.ReactionContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent
import javax.inject.Inject

class DisplayableEventFormatter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val markwon = MarkdownParser.markwonNotificationBuilder(context)

    fun format(timelineEvent: TimelineEvent, appendAuthor: Boolean): CharSequence {
        if (timelineEvent.root.isRedacted())
            return formatRedactedEvent(timelineEvent.root)

        if (timelineEvent.root.isEncrypted() && timelineEvent.root.mxDecryptionResult == null)
            return context.getString(R.string.encrypted_message)

        val senderName = timelineEvent.senderInfo.disambiguatedDisplayName

        return when (timelineEvent.root.getClearType()) {
            EventType.MESSAGE -> {
                timelineEvent.getLastMessageContent()?.let { messageContent ->
                    when (messageContent.msgType) {
                        MessageType.MSGTYPE_TEXT -> {
                            val body = timelineEvent.getTextEditableContent(false)
                            simpleFormat(
                                senderName,
                                markwon.toMarkdown(body),
                                appendAuthor
                            )
                        }

                        MessageType.MSGTYPE_IMAGE -> {
                            simpleFormat(
                                senderName, context.getString(R.string.sent_an_image), appendAuthor
                            )
                        }

                        MessageType.MSGTYPE_VIDEO -> {
                            simpleFormat(
                                senderName, context.getString(R.string.sent_a_video), appendAuthor
                            )
                        }

                        else -> {
                            simpleFormat(senderName, messageContent.body, appendAuthor)
                        }
                    }
                } ?: ""
            }

            EventType.REACTION -> {
                timelineEvent.root.getClearContent().toModel<ReactionContent>()?.relatesTo?.let {
                    val emojiSpanned = context.getString(R.string.sent_a_reaction, it.key)
                    simpleFormat(senderName, emojiSpanned, appendAuthor)
                } ?: ""
            }

            in EventType.POLL_START.values -> {
                (timelineEvent.getLastMessageContent() as? MessagePollContent)?.getBestPollCreationInfo()?.question?.getBestQuestion()
                    ?: context.getString(R.string.sent_a_poll)
            }

            in EventType.POLL_RESPONSE.values -> {
                context.getString(R.string.poll_response_room_list_preview)
            }

            in EventType.POLL_END.values -> {
                context.getString(R.string.poll_end_room_list_preview)
            }

            EventType.STATE_ROOM_THIRD_PARTY_INVITE -> formatRoomThirdPartyInvite(
                timelineEvent.root,
                senderName
            ) ?: context.getString(R.string.notification_new_invitation)

            else -> simpleFormat(
                senderName,
                context.getString(R.string.notifications),
                appendAuthor
            )
        }
    }

    private fun simpleFormat(
        senderName: String, body: CharSequence, appendAuthor: Boolean
    ): CharSequence {
        return if (appendAuthor) {
            StringBuilder(senderName).append(": ").append(body)
        } else {
            body
        }
    }

    private fun formatRedactedEvent(event: Event): String {
        return (event
            .unsignedData
            ?.redactedEvent
            ?.content
            ?.get("reason") as? String)
            ?.takeIf { it.isNotBlank() }
            .let { reason ->
                if (reason == null) {
                    if (event.isRedactedBySameUser()) {
                        context.getString(R.string.event_redacted_by_user_reason)
                    } else {
                        context.getString(R.string.event_redacted_by_admin_reason)
                    }
                } else {
                    if (event.isRedactedBySameUser()) {
                        context.getString(
                            R.string.event_redacted_by_user_reason_with_reason,
                            reason
                        )
                    } else {
                        context.getString(
                            R.string.event_redacted_by_admin_reason_with_reason,
                            reason
                        )
                    }
                }
            }
    }

    fun formatRoomThirdPartyInvite(
        event: Event,
        senderName: String?
    ): CharSequence? {
        val content = event.content.toModel<RoomThirdPartyInviteContent>()
        return content?.let {
            if (!event.isSentByCurrentUser()) {
                context.getString(
                    R.string.notice_room_third_party_invite, senderName
                )
            } else null
        }
    }

    private fun Event.isSentByCurrentUser() =
        senderId != null && senderId == MatrixSessionProvider.currentSession?.myUserId

}
