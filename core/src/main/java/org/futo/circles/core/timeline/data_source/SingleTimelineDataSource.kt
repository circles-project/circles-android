package org.futo.circles.core.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.timeline.builder.SingleTimelineBuilder
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import javax.inject.Inject

@ViewModelScoped
class SingleTimelineDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineBuilder: SingleTimelineBuilder
) : BaseTimelineDataSource(savedStateHandle, timelineBuilder) {

    private var timeline: Timeline? = null

    override fun startTimeline() {
        timeline = createAndStartNewTimeline(room)
    }

    override fun clearTimeline() {
        timeline?.let { closeTimeline(it) }
        timeline = null
    }

    override fun loadMore() = timeline?.let { loadNextPage(it) } ?: false

    override fun onTimelineFailure(throwable: Throwable) {
        timeline?.let { restartTimelineOnFailure(it) }
    }

}