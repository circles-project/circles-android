package com.futo.circles.ui.groups.timeline.data_source

import androidx.lifecycle.MutableLiveData
import com.futo.circles.extensions.nameOrId
import com.futo.circles.model.Post
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class GroupTimelineDatasource(
    private val roomId: String,
    private val timelineBuilder: GroupTimelineBuilder
) : Timeline.Listener {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    val timelineEventsLiveData = MutableLiveData<List<Post>>()

    private var timeline: Timeline? = null

    fun getGroupTitle() = room?.roomSummary()?.nameOrId() ?: roomId

    fun startTimeline() {
        timeline = room?.createTimeline(null, TimelineSettings(MESSAGES_PER_PAGE))?.apply {
            addListener(this@GroupTimelineDatasource)
            start()
        }
    }

    fun clearTimeline() {
        timeline?.apply {
            removeAllListeners()
            dispose()
        }
    }

    fun loadMore() {
        if (timeline?.hasMoreToLoad(Timeline.Direction.BACKWARDS) == true)
            timeline?.paginate(Timeline.Direction.BACKWARDS, MESSAGES_PER_PAGE)
    }

    fun toggleRepliesVisibility(eventId: String) {
        timelineEventsLiveData.value = timelineBuilder.toggleRepliesVisibilityFor(eventId)
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        timelineEventsLiveData.value = timelineBuilder.build(snapshot)
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timeline?.restartWithEventId(null)
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}