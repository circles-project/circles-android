package org.futo.circles.feature.timeline.data_source

import org.futo.circles.mapping.toPost
import org.futo.circles.model.Post
import org.futo.circles.model.PostContentType
import org.futo.circles.model.ReplyPost
import org.futo.circles.model.RootPost
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class TimelineBuilder {

    private val repliesVisibleEvents: MutableSet<String> = mutableSetOf()

    private var currentList: MutableList<Post> = mutableListOf()
    private var currentSnapshotMap: MutableMap<String, List<TimelineEvent>> = mutableMapOf()

    fun build(snapshot: List<TimelineEvent>): List<Post> {
        if (snapshot.isEmpty()) return currentList
        val list = processSnapshot(snapshot)
        val messageTimelineEvents = getOnlyMessageTimelineEvents(list)
        val posts = transformToPosts(messageTimelineEvents)
        val messagesWithReplies = setupRootMessagesWithReplies(posts)
        return handleRepliesVisibilityForPost(messagesWithReplies).also { currentList = it }
    }

    fun toggleRepliesVisibilityFor(eventId: String): List<Post> =
        if (isRepliesVisibleFor(eventId)) removeRepliesFromCurrentListFor(eventId)
        else addRepliesToCurrentListFor(eventId)

    private fun addRepliesToCurrentListFor(eventId: String): List<Post> {
        val list: MutableList<Post> = mutableListOf()
        repliesVisibleEvents.add(eventId)
        currentList.forEach { post ->
            if (post.id == eventId && post is RootPost) {
                list.add(post.copy(isRepliesVisible = true))
                list.addAll(post.replies)
            } else {
                list.add(post)
            }
        }
        return list.also { currentList = list }
    }

    private fun removeRepliesFromCurrentListFor(eventId: String): List<Post> {
        val list: MutableList<Post> = mutableListOf()
        repliesVisibleEvents.remove(eventId)
        val rootPostsList = getOnlyRootMessages(currentList)
        rootPostsList.forEach { rootPost ->
            if (rootPost.id == eventId) {
                list.add(rootPost.copy(isRepliesVisible = false))
            } else {
                list.add(rootPost)
                if (rootPost.isRepliesVisible) list.addAll(rootPost.replies)
            }
        }
        return list.also { currentList = list }
    }

    private fun processSnapshot(list: List<TimelineEvent>): List<TimelineEvent> {
        val roomId = list.firstOrNull()?.roomId ?: return emptyList()
        currentSnapshotMap[roomId] = list
        val fullTimelineEventList = mutableListOf<TimelineEvent>()
        currentSnapshotMap.values.forEach { fullTimelineEventList.addAll(it) }
        return fullTimelineEventList.sortedByDescending { it.root.originServerTs }
    }

    private fun handleRepliesVisibilityForPost(messagesWithReplies: List<RootPost>): MutableList<Post> {
        val list: MutableList<Post> = mutableListOf()
        messagesWithReplies.forEach { message ->
            list.add(message)
            if (message.isRepliesVisible) list.addAll(message.replies)
        }
        return list
    }

    private fun getOnlyMessageTimelineEvents(list: List<TimelineEvent>): List<TimelineEvent> =
        list.filter { it.root.getClearType() == EventType.MESSAGE }

    private fun isRepliesVisibleFor(id: String) = repliesVisibleEvents.contains(id)

    private fun transformToPosts(list: List<TimelineEvent>): List<Post> {
        return list.mapNotNull { timelineEvent ->
            getPostContentTypeFor(timelineEvent)?.let { contentType ->
                timelineEvent.toPost(contentType, isRepliesVisibleFor(timelineEvent.eventId))
            }
        }
    }

    private fun getPostContentTypeFor(event: TimelineEvent): PostContentType? {
        val messageType = event.root.getClearContent()?.toModel<MessageContent>()?.msgType
        return PostContentType.values().firstOrNull { it.typeKey == messageType }
    }

    private fun setupRootMessagesWithReplies(groupMessages: List<Post>): List<RootPost> {
        val rootMessages = getOnlyRootMessages(groupMessages)
        val replies = getOnlyRepliesMessages(groupMessages)
        val list = rootMessages.map { message ->
            val repliesForEvent = getRepliesFor(replies, message.id)
            message.copy(replies = repliesForEvent)
        }
        return list
    }

    private fun getOnlyRootMessages(list: List<Post>): List<RootPost> =
        list.filterIsInstance<RootPost>()

    private fun getOnlyRepliesMessages(list: List<Post>): List<ReplyPost> =
        list.filterIsInstance<ReplyPost>()

    private fun getRepliesFor(replies: List<ReplyPost>, eventId: String) =
        replies.filter { eventId == it.replyToId }

}