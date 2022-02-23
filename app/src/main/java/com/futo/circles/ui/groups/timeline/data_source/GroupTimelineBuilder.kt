package com.futo.circles.ui.groups.timeline.data_source

import com.futo.circles.mapping.toImageMessage
import com.futo.circles.mapping.toTextMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessageType
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class GroupTimelineBuilder {

    private val repliesVisibleEvents: MutableSet<String> = mutableSetOf()

    private var currentList: MutableList<GroupMessage> = mutableListOf()

    fun build(list: List<TimelineEvent>): List<GroupMessage> {
        val messageTimelineEvents = getOnlyMessageTimelineEvents(list)
        val groupMessages = transformToGroupMessages(messageTimelineEvents)
        val messagesWithReplies = setupRootMessagesWithVisibleReplies(groupMessages)
        return toFlatTimelineList(messagesWithReplies).also { currentList = it }
    }

    fun toggleRepliesVisibilityFor(eventId: String): List<GroupMessage> {
        val message = findMessageWithId(eventId) ?: return currentList

        val isRepliesVisible = message.generalMessageInfo.isRepliesVisible

        if (isRepliesVisible) removeRepliesFromCurrentList(message)
        else addRepliesToCurrentList(message)

        message.generalMessageInfo.isRepliesVisible = !isRepliesVisible

        return currentList
    }

    private fun findMessageWithId(eventId: String): GroupMessage? =
        currentList.firstOrNull { it.generalMessageInfo.id == eventId }

    private fun addRepliesToCurrentList(message: GroupMessage) {
        currentList.addAll(message.generalMessageInfo.replies)
        repliesVisibleEvents.add(message.generalMessageInfo.id)
    }

    private fun removeRepliesFromCurrentList(message: GroupMessage) {
        currentList.removeAll(message.generalMessageInfo.replies)
        repliesVisibleEvents.remove(message.generalMessageInfo.id)
    }

    private fun toFlatTimelineList(messagesWithReplies: List<GroupMessage>): MutableList<GroupMessage> {
        val list: MutableList<GroupMessage> = mutableListOf()
        messagesWithReplies.forEach { message ->
            list.add(message)
            if (message.generalMessageInfo.isRepliesVisible)
                list.addAll(message.generalMessageInfo.replies)
        }
        return list
    }

    private fun getOnlyMessageTimelineEvents(list: List<TimelineEvent>): List<TimelineEvent> =
        list.filter { it.root.getClearType() == EventType.MESSAGE }

    private fun isRepliesVisibleFor(id: String) = repliesVisibleEvents.contains(id)

    private fun transformToGroupMessages(list: List<TimelineEvent>): List<GroupMessage> {
        return list.mapNotNull {
            when (getMessageTypeEnumCaseFor(it)) {
                GroupMessageType.TEXT_MESSAGE -> it.toTextMessage(isRepliesVisibleFor(it.eventId))
                GroupMessageType.IMAGE_MESSAGE -> it.toImageMessage(isRepliesVisibleFor(it.eventId))
                else -> null
            }
        }
    }

    private fun getMessageTypeEnumCaseFor(event: TimelineEvent): GroupMessageType? {
        val messageType = event.root.getClearContent()?.toModel<MessageContent>()?.msgType
        return GroupMessageType.values().firstOrNull { it.typeKey == messageType }
    }

    private fun setupRootMessagesWithVisibleReplies(groupMessages: List<GroupMessage>): List<GroupMessage> {
        val rootMessages = getOnlyRootMessages(groupMessages)
        val replies = getOnlyRepliesMessages(groupMessages)
        rootMessages.forEach { message ->
            if (message.generalMessageInfo.isRepliesVisible) {
                val repliesForEvent = getRepliesFor(replies, message.generalMessageInfo.id)
                message.generalMessageInfo.replies.addAll(repliesForEvent)
            }
        }
        return rootMessages
    }

    private fun getOnlyRootMessages(list: List<GroupMessage>) =
        list.filter { !it.generalMessageInfo.isReply() }

    private fun getOnlyRepliesMessages(list: List<GroupMessage>) =
        list.filter { it.generalMessageInfo.isReply() }

    private fun getRepliesFor(replies: List<GroupMessage>, eventId: String) =
        replies.filter { eventId == it.generalMessageInfo.relationId }

}