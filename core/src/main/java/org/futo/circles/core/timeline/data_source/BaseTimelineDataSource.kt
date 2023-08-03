package org.futo.circles.core.timeline.data_source

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.Post
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.timeline.builder.TimelineBuilder
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings
import javax.inject.Inject


abstract class BaseTimelineDataSource(
    savedStateHandle: SavedStateHandle,
    private val timelineBuilder: TimelineBuilder
) : Timeline.Listener {

    class Factory @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val timelineBuilder: TimelineBuilder
    ) {
        fun create(isMultiTimelines: Boolean): BaseTimelineDataSource =
            if (isMultiTimelines) MultiTimelinesDataSource(savedStateHandle, timelineBuilder)
            else SingleTimelineDataSource(savedStateHandle, timelineBuilder)
    }

    protected open val listDirection = Timeline.Direction.BACKWARDS

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")
    protected val session = MatrixSessionProvider.getSessionOrThrow()
    val room = session.getRoom(roomId) ?: throw IllegalArgumentException("room is not found")

    val timelineEventsLiveData = MutableLiveData<List<Post>>()

    abstract fun startTimeline()
    abstract fun clearTimeline()
    abstract fun loadMore()

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        if (snapshot.isNotEmpty())
            timelineEventsLiveData.value = timelineBuilder.build(snapshot, false)
    }

    protected fun createAndStartNewTimeline(room: Room) = room.timelineService()
        .createTimeline(null, TimelineSettings(initialSize = MESSAGES_PER_PAGE))
        .apply {
            addListener(this@BaseTimelineDataSource)
            start()
        }

    protected fun closeTimeline(timeline: Timeline) {
        timeline.removeAllListeners()
        timeline.dispose()
    }

    protected fun loadNextPage(timeline: Timeline) {
        if (timeline.hasMoreToLoad(listDirection))
            timeline.paginate(listDirection, MESSAGES_PER_PAGE)
    }

    protected fun restartTimelineOnFailure(timeline: Timeline) {
        if (timeline.getPaginationState(listDirection).inError) timeline.restartWithEventId(null)
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }
}