package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.feature.timeline.builder.SingleTimelineBuilder
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.room.timeline.Timeline

//use Factory from BaseDataSource to inject
class SingleTimelineDataSource(
    savedStateHandle: SavedStateHandle,
    timelineBuilder: SingleTimelineBuilder,
    listDirection: Timeline.Direction
) : BaseTimelineDataSource(savedStateHandle, timelineBuilder, listDirection) {

    private var timeline: Timeline? = null

    override fun startTimeline(viewModelScope: CoroutineScope, listener: Timeline.Listener) {
        timeline = createAndStartNewTimeline(room, listener)
        viewModelScope.launch(Dispatchers.IO) { loadMore(false) }
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        tryOrNull { timeline?.restartWithEventId(null) }
    }

    override fun clearTimeline() {
        timeline?.let { closeTimeline(it) }
        timeline = null
    }

    override suspend fun loadMore(showLoader: Boolean) {
        timeline?.let { loadNextPage(showLoader, it) } ?: false
    }

}