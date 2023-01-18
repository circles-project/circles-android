package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.R
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.MessageType
import org.matrix.android.sdk.api.session.room.model.relation.ReactionContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getTextDisplayableContent

class DisplayableEventFormatter(
    private val context: Context,
    private val noticeEventFormatter: NoticeEventFormatter,
) {

    fun format(timelineEvent: TimelineEvent, isDm: Boolean, appendAuthor: Boolean): CharSequence {
        if (timelineEvent.root.isRedacted())
            return noticeEventFormatter.formatRedactedEvent(timelineEvent.root)

        if (timelineEvent.root.isEncrypted() && timelineEvent.root.mxDecryptionResult == null)
            return context.getString(R.string.encrypted_message)

        val senderName = timelineEvent.senderInfo.disambiguatedDisplayName

        return when (timelineEvent.root.getClearType()) {
            EventType.MESSAGE -> {
                timelineEvent.getLastMessageContent()?.let { messageContent ->
                    when (messageContent.msgType) {
                        MessageType.MSGTYPE_TEXT -> {
                            val body = messageContent.getTextDisplayableContent()
                            simpleFormat(senderName, body, appendAuthor)
                        }
                        MessageType.MSGTYPE_VERIFICATION_REQUEST -> {
                            simpleFormat(
                                senderName,
                                context.getString(R.string.verification_request),
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
            else -> noticeEventFormatter.format(timelineEvent, isDm) ?: ""
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

}
