package org.futo.circles.core.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.timeline.builder.BaseTimelineBuilder
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

@ViewModelScoped
class SingleTimelineDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val timelineBuilder: BaseTimelineBuilder
) : BaseTimelineDataSource(savedStateHandle) {

    private val threadEventId: String? = savedStateHandle["threadEventId"]
    private val isThread: Boolean = threadEventId != null
    override val listDirection =
        if (isThread) Timeline.Direction.FORWARDS else Timeline.Direction.BACKWARDS

    private var timeline: Timeline? = null

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        if (snapshot.isNotEmpty())
            timelineEventsLiveData.value = timelineBuilder.build(snapshot, isThread)
    }

    override fun startTimeline() {
        timeline = createAndStartNewTimeline(room, threadEventId)
    }

    override fun clearTimeline() {
        timeline?.let { closeTimeline(it) }
        timeline = null
    }

    override fun loadMore() {
        timeline?.let { loadNextPage(it) }
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timeline?.let { restartTimelineOnFailure(it) }
    }

}