package com.futo.circles.feature.timeline.data_source

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.bumptech.glide.Glide
import com.futo.circles.core.matrix.room.RoomRelationsBuilder
import com.futo.circles.extensions.*
import com.futo.circles.feature.timeline.post.share.ImageShareable
import com.futo.circles.feature.timeline.post.share.TextShareable
import com.futo.circles.mapping.nameOrId
import com.futo.circles.model.*
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

class TimelineDataSource(
    roomId: String,
    private val type: CircleRoomTypeArg,
    private val context: Context,
    private val timelineBuilder: TimelineBuilder
) : Timeline.Listener {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)

    val roomTitleLiveData = room?.getRoomSummaryLive()?.map { it.getOrNull()?.nameOrId() }
    val timelineEventsLiveData = MutableLiveData<List<Post>>()
    val accessLevelFlow =
        room?.stateService()?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS)?.asFlow()
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
        timelineEventsLiveData.value = timelineBuilder.build(snapshot)
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timelines.forEach { it.restartWithEventId(null) }
    }

    fun toggleRepliesVisibility(eventId: String) {
        timelineEventsLiveData.value = timelineBuilder.toggleRepliesVisibilityFor(eventId)
    }

    fun sendTextMessage(roomId: String, message: String, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId)
        threadEventId?.let { roomForMessage?.relationService()?.replyInThread(it, message) }
            ?: roomForMessage?.sendService()?.sendTextMessage(message)
    }

    fun sendImage(roomId: String, uri: Uri, threadEventId: String?) {
        val roomForMessage = session?.getRoom(roomId)
        uri.toImageContentAttachmentData(context)?.let {
            roomForMessage?.sendService()?.sendMedia(it, true, emptySet(), threadEventId)
        }
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