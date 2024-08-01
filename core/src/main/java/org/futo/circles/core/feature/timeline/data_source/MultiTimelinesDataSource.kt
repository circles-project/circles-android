package org.futo.circles.core.feature.timeline.data_source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.utils.getTimelines
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isReply

//use Factory from BaseDataSource to inject
class MultiTimelinesDataSource(preferencesProvider: PreferencesProvider) :
    BaseTimelineDataSource(preferencesProvider) {

    private var timelines: MutableList<Timeline> = mutableListOf()
    private var currentSnapshotMap: MutableMap<String, List<Post>> = mutableMapOf()
    private var readReceiptMap: MutableMap<String, List<Long>> = mutableMapOf()

    override fun startTimeline(
        viewModelScope: CoroutineScope,
        listener: Timeline.Listener
    ) {
        getTimelines(listOf(Membership.JOIN)).forEach { roomSummary ->
            session.getRoom(roomSummary.roomId)?.let { room ->
                val timeline = createAndStartNewTimeline(room, listener)
                timelines.add(timeline)
            }
        }
        viewModelScope.launch(Dispatchers.IO) { loadMore(false) }
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        tryOrNull {
            timelines.firstOrNull { it.timelineID == timelineId }?.restartWithEventId(null)
        }
    }

    override val listDirection: Timeline.Direction get() = Timeline.Direction.BACKWARDS

    override fun clearTimeline() {
        timelines.forEach { timeline -> closeTimeline(timeline) }
        timelines.clear()
    }

    override suspend fun loadMore(showLoader: Boolean) {
        timelines.map { timeline -> loadNextPage(showLoader, timeline) }
    }

    override suspend fun processSnapshot(
        snapshot: List<TimelineEvent>,
        roomId: String
    ): List<Post> {
        val room = MatrixSessionProvider.currentSession?.getRoom(roomId)
            ?: return getCurrentTimelinesPostsList()
        val roomName = room.roomSummary()?.nameOrId()
        val roomOwner = getRoomOwner(roomId)
        val receipts = getReadReceipts(room).also { readReceiptMap[roomId] = it }
        currentSnapshotMap[roomId] =
            snapshot.filterRootPostNotFromOwner(receipts, roomName, roomOwner)
        return sortList(getCurrentTimelinesPostsList())
    }

    override fun filterTimelineEvents(snapshot: List<TimelineEvent>): List<TimelineEvent> =
        snapshot.filter {
            it.isSupportedEvent() && it.isNotRemovedEvent() && !it.isReply()
        }
    
    private fun List<TimelineEvent>.filterRootPostNotFromOwner(
        receipts: List<Long>,
        roomName: String?,
        roomOwner: RoomMemberSummary?
    ): List<Post> {
        val roomOwnerId = roomOwner?.userId
        val roomOwnerName = roomOwner?.notEmptyDisplayName()

        return mapNotNull {
            if (roomOwnerId == it.senderInfo.userId) it.toPost(receipts, roomName, roomOwnerName)
            else null
        }
    }

    private fun getCurrentTimelinesPostsList() = currentSnapshotMap.flatMap { (_, value) -> value }

}