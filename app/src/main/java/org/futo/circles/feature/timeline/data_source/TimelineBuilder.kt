package org.futo.circles.feature.timeline.data_source

import org.futo.circles.mapping.toPost
import org.futo.circles.model.Post
import org.futo.circles.model.PostContentType
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import org.matrix.android.sdk.api.session.room.timeline.isReply

class TimelineBuilder {

    private var currentList: MutableList<Post> = mutableListOf()
    private var currentSnapshotMap: MutableMap<String, List<TimelineEvent>> = mutableMapOf()
    private var lastReadEventTime: Long? = null

    private val supportedTimelineEvens: List<String> = mutableListOf(EventType.MESSAGE).apply {
        addAll(EventType.POLL_START.values)
    }

    fun build(snapshot: List<TimelineEvent>, isThread: Boolean): List<Post> {
        if (snapshot.isEmpty()) return currentList
        val list = processSnapshot(snapshot, isThread)
        val messageTimelineEvents = getOnlySupportedTimelineEvents(list)
        return transformToPosts(messageTimelineEvents).also { currentList = it.toMutableList() }
    }

    private fun processSnapshot(list: List<TimelineEvent>, isThread: Boolean): List<TimelineEvent> {
        val filteredList = filterTimelineEvents(list, isThread)
        val roomId = filteredList.firstOrNull()?.roomId ?: return emptyList()
        currentSnapshotMap[roomId] = filteredList
        val fullTimelineEventList = mutableListOf<TimelineEvent>()
        currentSnapshotMap.values.forEach { fullTimelineEventList.addAll(it) }
        return if (isThread) fullTimelineEventList.sortedBy { it.root.originServerTs }
        else fullTimelineEventList.sortedByDescending { it.root.originServerTs }
    }

    private fun filterTimelineEvents(
        list: List<TimelineEvent>,
        isThread: Boolean
    ): List<TimelineEvent> = list.filter {
        val removedEventsCondition = !it.isEdition() && !it.root.isRedacted()
        if (isThread) removedEventsCondition
        else removedEventsCondition && !it.isReply()
    }

    private fun getOnlySupportedTimelineEvents(list: List<TimelineEvent>): List<TimelineEvent> =
        list.filter { it.root.getClearType() in supportedTimelineEvens }

    private fun transformToPosts(list: List<TimelineEvent>): List<Post> {
        setCurrentReadReceiptTime(list.firstOrNull()?.roomId ?: "")
        return list.mapNotNull { timelineEvent ->
            getPostContentTypeFor(timelineEvent)?.let { contentType ->
                timelineEvent.toPost(contentType, lastReadEventTime ?: 0)
            }
        }
    }

    private fun setCurrentReadReceiptTime(roomId: String) {
        val session = MatrixSessionProvider.currentSession ?: return
        val room = session.getRoom(roomId) ?: return
        val readEventId = room.readService().getUserReadReceipt(session.myUserId) ?: return
        val readEventTime = room.getTimelineEvent(readEventId)?.root?.originServerTs ?: return
        if (lastReadEventTime == null) lastReadEventTime = readEventTime
    }

    private fun getPostContentTypeFor(event: TimelineEvent): PostContentType? {
        val messageType = event.getLastMessageContent()?.msgType
        return PostContentType.values().firstOrNull { it.typeKey == messageType }
    }
}