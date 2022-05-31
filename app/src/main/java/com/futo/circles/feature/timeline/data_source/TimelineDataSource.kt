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
    private val roomId: String,
    private val type: CircleRoomTypeArg,
    private val context: Context,
    private val timelineBuilder: TimelineBuilder,
    private val roomRelationsBuilder: RoomRelationsBuilder
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
        session?.userService()?.ignoreUserIds(listOf(userId))
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

    fun removeMessage(roomId: String, eventId: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.getTimelineEvent(eventId)
            ?.let { roomForMessage.sendService().redactEvent(it.root, null) }
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.sendReaction(eventId, emoji)
    }

    suspend fun unSendReaction(roomId: String, eventId: String, emoji: String) = createResult {
        val roomForMessage = session?.getRoom(roomId)
        roomForMessage?.relationService()?.undoReaction(eventId, emoji)
    }

    suspend fun leaveGroup() =
        createResult { session?.roomService()?.leaveRoom(roomId) }

    suspend fun deleteCircle() = createResult {
        room?.roomSummary()?.spaceChildren?.forEach {
            roomRelationsBuilder.removeRelations(it.childRoomId, roomId)
        }
        getTimelineRoomFor(roomId)?.let { timelineRoom ->
            timelineRoom.roomSummary()?.otherMemberIds?.forEach { memberId ->
                timelineRoom.membershipService().ban(memberId)
            }
            session?.roomService()?.leaveRoom(timelineRoom.roomId)
        }
        session?.roomService()?.leaveRoom(roomId)
    }

    private fun getTimelineRooms(): List<Room> = when (type) {
        CircleRoomTypeArg.Circle -> room?.roomSummary()?.spaceChildren?.mapNotNull {
            session?.getRoom(it.childRoomId)
        } ?: emptyList()
        else -> listOfNotNull(room)
    }

    fun isUserSingleRoomOwner(): Boolean {
        val isUserOwner = getCurrentUserPowerLevel(roomId) == Role.Admin.value
        return isUserOwner && getRoomOwners(roomId).size == 1
    }

    companion object {
        private const val MESSAGES_PER_PAGE = 30
    }
}