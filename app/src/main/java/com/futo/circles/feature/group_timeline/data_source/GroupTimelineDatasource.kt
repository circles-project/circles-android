package com.futo.circles.feature.group_timeline.data_source

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.toImageContentAttachmentData
import com.futo.circles.mapping.nameOrId
import com.futo.circles.model.Post
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class GroupTimelineDatasource(
    private val roomId: String,
    private val context: Context,
    private val timelineBuilder: GroupTimelineBuilder
) : Timeline.Listener {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    val timelineEventsLiveData = MutableLiveData<List<Post>>()
    val accessLevelFlow = room?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS)?.asFlow()
        ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()

    private var timeline: Timeline? = null

    val roomTitleLiveData = room?.getRoomSummaryLive()?.map { it.getOrNull()?.nameOrId() }

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

    suspend fun leaveGroup() =
        createResult { MatrixSessionProvider.currentSession?.leaveRoom(roomId) }

    fun sendTextMessage(message: String) = room?.sendTextMessage(message)

    fun sendImage(uri: Uri, threadEventId: String?) {
        uri.toImageContentAttachmentData(context)?.let {
            room?.sendMedia(it, true, emptySet(), threadEventId)
        }
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}