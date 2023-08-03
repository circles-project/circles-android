package org.futo.circles.core.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.timeline.builder.BaseTimelineBuilder
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

@ViewModelScoped
class MultiTimelinesDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val timelineBuilder: BaseTimelineBuilder
) : BaseTimelineDataSource(savedStateHandle) {

    private var timelines: MutableList<Timeline> = mutableListOf()
    override val listDirection: Timeline.Direction = Timeline.Direction.BACKWARDS

    override fun startTimeline() {
        getTimelineRooms().forEach { room ->
            val timeline = createAndStartNewTimeline(room)
            timelines.add(timeline)
        }
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        if (snapshot.isNotEmpty())
            timelineEventsLiveData.value = timelineBuilder.build(snapshot, false)
    }

    override fun clearTimeline() {
        timelines.forEach { timeline -> closeTimeline(timeline) }
        timelines.clear()
    }

    override fun loadMore() {
        timelines.forEach { timeline -> loadNextPage(timeline) }
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timelines.forEach { restartTimelineOnFailure(it) }
    }

    private fun getTimelineRooms(): List<Room> = room.roomSummary()?.spaceChildren?.mapNotNull {
        session.getRoom(it.childRoomId)
    } ?: emptyList()
}