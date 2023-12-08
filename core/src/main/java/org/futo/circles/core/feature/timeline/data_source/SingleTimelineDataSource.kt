package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.feature.timeline.builder.SingleTimelineBuilder
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import javax.inject.Inject

@ViewModelScoped
class SingleTimelineDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineBuilder: SingleTimelineBuilder
) : BaseTimelineDataSource(savedStateHandle, timelineBuilder) {

    private var timeline: Timeline? = null

    override fun startTimeline(listener: Timeline.Listener) {
        timeline = createAndStartNewTimeline(room, listener)
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        timeline?.restartWithEventId(null)
    }

    override fun clearTimeline() {
        timeline?.let { closeTimeline(it) }
        timeline = null
    }

    override fun loadMore() = timeline?.let { loadNextPage(it) } ?: false

}