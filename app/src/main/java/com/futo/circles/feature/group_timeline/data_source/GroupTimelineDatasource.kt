package com.futo.circles.feature.group_timeline.data_source

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

    fun sendTextMessage(message: String, threadEventId: String?) {
        threadEventId?.let { room?.replyInThread(it, message) } ?: room?.sendTextMessage(message)
    }

    fun sendImage(uri: Uri, threadEventId: String?) {
        uri.toImageContentAttachmentData(context)?.let {
            room?.sendMedia(it, true, emptySet(), threadEventId)
        }
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

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }

}