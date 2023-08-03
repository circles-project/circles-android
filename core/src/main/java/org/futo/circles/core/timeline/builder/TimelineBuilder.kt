package org.futo.circles.core.timeline.builder

import org.futo.circles.core.extensions.getPostContentType
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import org.matrix.android.sdk.api.session.room.timeline.isReply
import javax.inject.Inject

class TimelineBuilder @Inject constructor() {

    private var currentList: MutableList<Post> = mutableListOf()
    private var currentSnapshotMap: MutableMap<String, List<TimelineEvent>> = mutableMapOf()
    private var lastReadEventTime: Long? = null

    private val supportedTimelineEvens: List<String> = mutableListOf(EventType.MESSAGE).apply {
        addAll(EventType.POLL_START.values)
    }

    fun build(snapshot: List<TimelineEvent>, isThread: Boolean): List<Post> {
        if (snapshot.isEmpty()) return currentList
        val list = processSnapshot(snapshot, isThread)
        return transformToPosts(list).also { currentList = it.toMutableList() }
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
    ): List<TimelineEvent> {
        val filteredList = list.filter {
            val removedEventsCondition = !it.isEdition() && !it.root.isRedacted()
            if (isThread) removedEventsCondition
            else removedEventsCondition && !it.isReply()
        }
        return getOnlySupportedTimelineEvents(filteredList)
    }

    private fun getOnlySupportedTimelineEvents(list: List<TimelineEvent>): List<TimelineEvent> =
        list.filter { it.root.getClearType() in supportedTimelineEvens }

    private fun transformToPosts(list: List<TimelineEvent>): List<Post> {
        setCurrentReadReceiptTime(list.firstOrNull()?.roomId ?: "")
        return list.mapNotNull { timelineEvent ->
            timelineEvent.getPostContentType()?.let { contentType ->
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
}