package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.futo.circles.core.feature.timeline.builder.MultiTimelineBuilder
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline

//use Factory from BaseDataSource to inject
class MultiTimelinesDataSource(
    savedStateHandle: SavedStateHandle,
    timelineBuilder: MultiTimelineBuilder,
    listDirection: Timeline.Direction
) : BaseTimelineDataSource(savedStateHandle, timelineBuilder, listDirection) {

    private var timelines: MutableList<Timeline> = mutableListOf()

    override fun startTimeline(
        viewModelScope: CoroutineScope,
        listener: Timeline.Listener
    ) {
        getTimelineRooms().forEach { room ->
            val timeline = createAndStartNewTimeline(room, listener)
            timelines.add(timeline)
        }
        viewModelScope.launch(Dispatchers.IO) { loadMore(false) }
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        tryOrNull {
            timelines.firstOrNull { it.timelineID == timelineId }?.restartWithEventId(null)
        }
    }

    override fun clearTimeline() {
        timelines.forEach { timeline -> closeTimeline(timeline) }
        timelines.clear()
    }

    override suspend fun loadMore(showLoader: Boolean) {
        timelines.map { timeline -> loadNextPage(showLoader, timeline) }
    }

    private fun getTimelineRooms(): List<Room> = room.roomSummary()?.spaceChildren?.mapNotNull {
        session.getRoom(it.childRoomId)
    } ?: emptyList()
}