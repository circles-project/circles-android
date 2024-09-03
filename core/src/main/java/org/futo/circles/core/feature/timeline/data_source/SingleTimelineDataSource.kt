package org.futo.circles.core.feature.timeline.data_source

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.extensions.getRoomOwner
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.TimelineTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.isReply

//use Factory from BaseDataSource to inject
class SingleTimelineDataSource(
    preferencesProvider: PreferencesProvider,
    private val timelineType: TimelineTypeArg,
    roomId: String,
    private val threadEventId: String?
) : BaseTimelineDataSource(preferencesProvider) {

    private val room =
        session.getRoom(roomId) ?: throw IllegalArgumentException("room is not found")

    private val isThread = timelineType == TimelineTypeArg.THREAD

    private var timeline: Timeline? = null

    private var currentSnapshotList: List<Post> = listOf()

    override fun startTimeline(viewModelScope: CoroutineScope, listener: Timeline.Listener) {
        timeline = createAndStartNewTimeline(room, listener, threadEventId)
        viewModelScope.launch(Dispatchers.IO) { loadMore(false) }
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        tryOrNull { timeline?.restartWithEventId(null) }
    }

    override val listDirection: Timeline.Direction
        get() = when (timelineType) {
            TimelineTypeArg.DM, TimelineTypeArg.THREAD -> Timeline.Direction.FORWARDS
            else -> Timeline.Direction.BACKWARDS
        }

    override fun clearTimeline() {
        timeline?.let { closeTimeline(it) }
        timeline = null
    }

    override suspend fun loadMore(showLoader: Boolean) {
        timeline?.let { loadNextPage(showLoader, it) } ?: false
    }

    override suspend fun processSnapshot(
        snapshot: List<TimelineEvent>,
        roomId: String,
    ): List<Post> {
        val room =
            MatrixSessionProvider.currentSession?.getRoom(roomId) ?: return currentSnapshotList
        val roomName = room.roomSummary()?.nameOrId()
        val roomOwnerName = getRoomOwner(roomId)?.notEmptyDisplayName()
        val posts = snapshot.map {
            it.toPost(
                if (isThread) roomName else null,
                if (isThread) roomOwnerName else null
            )
        }
        currentSnapshotList = posts
        return sortList(posts)
    }

    override fun filterTimelineEvents(snapshot: List<TimelineEvent>): List<TimelineEvent> =
        snapshot.filter {
            if (isThread) it.isSupportedEvent() && it.isNotRemovedEvent()
            else it.isSupportedEvent() && it.isNotRemovedEvent() && !it.isReply()
        }

}