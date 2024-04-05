package org.futo.circles.core.feature.timeline.builder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isEdition
import org.matrix.android.sdk.api.session.room.timeline.isReply

abstract class BaseTimelineBuilder(private val preferencesProvider: PreferencesProvider) {

    private val supportedTimelineEvens: List<String> =
        listOf(EventType.MESSAGE, EventType.POLL_START.stable, EventType.POLL_START.unstable)

    abstract suspend fun processSnapshot(
        snapshot: List<TimelineEvent>,
        roomId: String,
        isThread: Boolean
    ): List<Post>

    suspend fun build(
        roomId: String,
        snapshot: List<TimelineEvent>,
        isThread: Boolean
    ): List<Post> =
        withContext(Dispatchers.IO) {
            val filteredEvents = filterTimelineEvents(snapshot, isThread)
            processSnapshot(filteredEvents, roomId, isThread)
        }

    protected fun sortList(list: List<Post>, isThread: Boolean) =
        if (isThread) list.sortedBy { it.postInfo.timestamp }
        else list.sortedByDescending { it.postInfo.timestamp }

    protected fun getReadReceipts(room: Room): List<Long> =
        room.membershipService().getRoomMembers(roomMemberQueryParams {
            memberships = listOf(Membership.JOIN)
        }).map {
            val eventId = room.readService().getUserReadReceipt(it.userId)
                ?: return@map System.currentTimeMillis()
            room.getTimelineEvent(eventId)?.root?.originServerTs ?: 0
        }

    fun filterTimelineEvents(
        snapshot: List<TimelineEvent>,
        isThread: Boolean
    ): List<TimelineEvent> = snapshot.filter {
        val isSupportedEvent = if (preferencesProvider.isDeveloperModeEnabled()) true
        else it.root.getClearType() in supportedTimelineEvens
        val isNotRemovedEvent = !it.isEdition() && !it.root.isRedacted()

        if (isThread) isSupportedEvent && isNotRemovedEvent
        else isSupportedEvent && isNotRemovedEvent && !it.isReply()
    }
}