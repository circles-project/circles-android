package com.futo.circles.core.matrix.timeline.data_source

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.bumptech.glide.Glide
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.getUri
import com.futo.circles.extensions.saveImageToGallery
import com.futo.circles.extensions.toImageContentAttachmentData
import com.futo.circles.feature.share.ImageShareable
import com.futo.circles.feature.share.TextShareable
import com.futo.circles.mapping.nameOrId
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import com.futo.circles.model.PostContent
import com.futo.circles.model.TextContent
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.timeline.Timeline
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings

abstract class BaseTimelineDataSource(
    roomId: String,
    private val context: Context,
    private val timelineBuilder: GroupTimelineBuilder
) : Timeline.Listener {

    protected val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    val roomTitleLiveData = room?.getRoomSummaryLive()?.map { it.getOrNull()?.nameOrId() }
    val timelineEventsLiveData = MutableLiveData<List<Post>>()
    val accessLevelFlow = room?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS)?.asFlow()
        ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()

    private var timelines: MutableList<Timeline> = mutableListOf()
    abstract fun getTimelineRooms(): List<Room>

    fun startTimeline() {
        getTimelineRooms().forEach { room ->
            val timeline = room.createTimeline(null, TimelineSettings(MESSAGES_PER_PAGE)).apply {
                addListener(this@BaseTimelineDataSource)
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
        timelines.forEach {timeline->
            if (timeline.hasMoreToLoad(Timeline.Direction.BACKWARDS))
                timeline.paginate(Timeline.Direction.BACKWARDS, MESSAGES_PER_PAGE)
        }
    }

    fun toggleRepliesVisibility(eventId: String) {
        timelineEventsLiveData.value = timelineBuilder.toggleRepliesVisibilityFor(eventId)
    }

    override fun onTimelineUpdated(snapshot: List<TimelineEvent>) {
        timelineEventsLiveData.value = timelineBuilder.build(snapshot)
    }

    override fun onTimelineFailure(throwable: Throwable) {
        timelines.forEach { it.restartWithEventId(null) }
    }

    suspend fun getShareableContent(content: PostContent) = withContext(Dispatchers.IO) {
        when (content) {
            is ImageContent -> {
                val uri = Glide.with(context).asFile().load(content).submit().get().getUri(context)
                ImageShareable(uri)
            }
            is TextContent -> TextShareable(content.message)
        }
    }

    suspend fun saveImage(imageContent: ImageContent) = withContext(Dispatchers.IO) {
        val b = Glide.with(context).asBitmap().load(imageContent).submit().get()
        b.saveImageToGallery(context)
    }

    suspend fun ignoreSender(userId: String) = createResult {
        MatrixSessionProvider.currentSession?.ignoreUserIds(listOf(userId))
    }

    fun sendTextMessage(roomId: String, message: String, threadEventId: String?) {
        val roomForMessage = MatrixSessionProvider.currentSession?.getRoom(roomId)
        threadEventId?.let { roomForMessage?.replyInThread(it, message) }
            ?: roomForMessage?.sendTextMessage(message)
    }

    fun sendImage(roomId: String, uri: Uri, threadEventId: String?) {
        val roomForMessage = MatrixSessionProvider.currentSession?.getRoom(roomId)
        uri.toImageContentAttachmentData(context)?.let {
            roomForMessage?.sendMedia(it, true, emptySet(), threadEventId)
        }
    }

    fun removeMessage(roomId: String, eventId: String) {
        val roomForMessage = MatrixSessionProvider.currentSession?.getRoom(roomId)
        roomForMessage?.getTimelineEvent(eventId)?.let { roomForMessage.redactEvent(it.root, null) }
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        val roomForMessage = MatrixSessionProvider.currentSession?.getRoom(roomId)
        roomForMessage?.sendReaction(eventId, emoji)
    }

    suspend fun unSendReaction(roomId: String, eventId: String, emoji: String) = createResult {
        val roomForMessage = MatrixSessionProvider.currentSession?.getRoom(roomId)
        roomForMessage?.undoReaction(eventId, emoji)
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}