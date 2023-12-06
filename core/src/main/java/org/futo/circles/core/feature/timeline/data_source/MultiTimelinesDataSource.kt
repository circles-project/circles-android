package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.feature.timeline.builder.MultiTimelineBuilder
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import javax.inject.Inject

@ViewModelScoped
class MultiTimelinesDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    timelineBuilder: MultiTimelineBuilder
) : BaseTimelineDataSource(savedStateHandle, timelineBuilder) {

    private var timelines: MutableList<Timeline> = mutableListOf()

    override fun startTimeline(listener: Timeline.Listener) {
        getTimelineRooms().forEach { room ->
            val timeline = createAndStartNewTimeline(room, listener)
            timelines.add(timeline)
        }
    }

    override fun onRestartTimeline(timelineId: String, throwable: Throwable) {
        timelines.firstOrNull { it.timelineID == timelineId }?.restartWithEventId(null)
    }

    override fun clearTimeline() {
        timelines.forEach { timeline -> closeTimeline(timeline) }
        timelines.clear()
    }

    override fun loadMore(): Boolean {
        var hasMore = false
        timelines.forEach { timeline ->
            if (loadNextPage(timeline)) hasMore = true
        }
        return hasMore
    }

    private fun getTimelineRooms(): List<Room> = room.roomSummary()?.spaceChildren?.mapNotNull {
        session.getRoom(it.childRoomId)
    } ?: emptyList()
}