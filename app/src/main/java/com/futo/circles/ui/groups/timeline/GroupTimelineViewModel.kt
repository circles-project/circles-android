package com.futo.circles.ui.groups.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.nameOrId
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class GroupTimelineViewModel(
    roomId: String,
    matrixSessionProvider: MatrixSessionProvider
) : ViewModel(), Timeline.Listener {

    private val room = matrixSessionProvider.currentSession?.getRoom(roomId)

    val titleLiveData = MutableLiveData(room?.roomSummary()?.nameOrId() ?: roomId)
    val timelineEventsLiveData = MutableLiveData<List<TimelineEvent>>()

    private val timeline = room?.createTimeline(null, TimelineSettings(MESSAGES_PER_PAGE))?.apply {
        addListener(this@GroupTimelineViewModel)
        start()
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        timelineEventsLiveData.value = snapshot
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timeline?.restartWithEventId(null)
    }

    fun loadMore() {
        if (timeline?.hasMoreToLoad(Timeline.Direction.BACKWARDS) == true)
            timeline.paginate(Timeline.Direction.BACKWARDS, MESSAGES_PER_PAGE)
    }

    override fun onCleared() {
        timeline?.apply {
            removeAllListeners()
            dispose()
        }
        super.onCleared()
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}