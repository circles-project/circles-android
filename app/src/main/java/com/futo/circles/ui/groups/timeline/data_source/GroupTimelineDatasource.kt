package com.futo.circles.ui.groups.timeline.data_source

import androidx.lifecycle.MutableLiveData
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class GroupTimelineDatasource(
    private val roomId: String,
    matrixSessionProvider: MatrixSessionProvider
) : Timeline.Listener {

    private val room = matrixSessionProvider.currentSession?.getRoom(roomId)

    val timelineEventsLiveData = MutableLiveData<List<TimelineEvent>>()

    private var timeline: Timeline? = null


    fun getGroupTitle() = room?.roomSummary()?.nameOrId() ?: roomId

    fun startTimeline() {
        room?.createTimeline(null, TimelineSettings(MESSAGES_PER_PAGE))?.apply {
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

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        timelineEventsLiveData.value = snapshot
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timeline?.restartWithEventId(null)
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}