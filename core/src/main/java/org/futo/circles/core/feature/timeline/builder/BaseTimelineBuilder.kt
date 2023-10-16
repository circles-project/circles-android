package org.futo.circles.core.feature.timeline.builder

import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import org.matrix.android.sdk.api.session.room.timeline.isReply

abstract class BaseTimelineBuilder {

    private val supportedTimelineEvens: List<String> =
        listOf(EventType.MESSAGE, EventType.POLL_START.stable, EventType.POLL_START.unstable)

    abstract fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post>

    fun build(snapshot: List<TimelineEvent>, isThread: Boolean): List<Post> = snapshot
        .filterTimelineEvents(isThread)
        .processSnapshot(isThread)

    protected fun sortList(list: List<Post>, isThread: Boolean) =
        if (isThread) list.sortedBy { it.postInfo.timestamp }
        else list.sortedByDescending { it.postInfo.timestamp }

    protected fun getReadReceipts(room: Room): List<Long> =
        MatrixSessionProvider.currentSession?.getRoomSummary(room.roomId)?.otherMemberIds?.map {
            val eventId = room.readService().getUserReadReceipt(it)
                ?: return@map System.currentTimeMillis()
            room.getTimelineEvent(eventId)?.root?.originServerTs ?: 0
        } ?: emptyList()

    private fun List<TimelineEvent>.filterTimelineEvents(isThread: Boolean): List<TimelineEvent> =
        filter {
            val isSupportedEvent = it.root.getClearType() in supportedTimelineEvens
            val isNotRemovedEvent = !it.isEdition() && !it.root.isRedacted()

            if (isThread) isSupportedEvent && isNotRemovedEvent
            else isSupportedEvent && isNotRemovedEvent && !it.isReply()
        }
}