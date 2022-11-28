package org.futo.circles.feature.timeline.data_source

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.mapping.nameOrId
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.Post
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class TimelineDataSource(
    roomId: String,
    private val type: CircleRoomTypeArg,
    private val timelineBuilder: TimelineBuilder
) : Timeline.Listener {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)

    val roomTitleLiveData = room?.getRoomSummaryLive()?.map { it.getOrNull()?.nameOrId() }
    val timelineEventsLiveData = MutableLiveData<List<Post>>()
    val accessLevelFlow =
        room?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()

    private var timelines: MutableList<Timeline> = mutableListOf()

    fun startTimeline() {
        getTimelineRooms().forEach { room ->
            val timeline =
                room.timelineService().createTimeline(null, TimelineSettings(MESSAGES_PER_PAGE))
                    .apply {
                        addListener(this@TimelineDataSource)
                        start()
                    }
            timelines.add(timeline)
        }
    }

    fun clearTimeline() {
        timelines.forEach { timeline ->
            timeline.removeAllListeners()
            timeline.dispose()
        }
        timelines.clear()
    }

    fun loadMore() {
        timelines.forEach { timeline ->
            if (timeline.hasMoreToLoad(Timeline.Direction.BACKWARDS))
                timeline.paginate(Timeline.Direction.BACKWARDS, MESSAGES_PER_PAGE)
        }
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        if (snapshot.isNotEmpty())
            timelineEventsLiveData.value = timelineBuilder.build(snapshot)
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timelines.forEach { it.restartWithEventId(null) }
    }

    fun toggleRepliesVisibility(eventId: String) {
        timelineEventsLiveData.value = timelineBuilder.toggleRepliesVisibilityFor(eventId)
    }


    private fun getTimelineRooms(): List<Room> = when (type) {
        CircleRoomTypeArg.Circle -> room?.roomSummary()?.spaceChildren?.mapNotNull {
            session?.getRoom(it.childRoomId)
        } ?: emptyList()
        else -> listOfNotNull(room)
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }
}