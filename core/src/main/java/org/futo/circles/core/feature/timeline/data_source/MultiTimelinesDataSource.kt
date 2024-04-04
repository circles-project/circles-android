package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    override suspend fun loadMore(viewModelScope: CoroutineScope) {
        timelines.map { timeline ->
            viewModelScope.async { loadNextPage(timeline) }
        }.awaitAll()
    }

    override fun loadMore() {
        timelines.forEach { timeline -> silentLoadNextPage(timeline) }
    }

    private fun getTimelineRooms(): List<Room> = room.roomSummary()?.spaceChildren?.mapNotNull {
        session.getRoom(it.childRoomId)
    } ?: emptyList()
}