package org.futo.circles.feature.timeline

import androidx.lifecycle.asLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.people.UserOptionsDataSource
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.room.RoomNotificationsDataSource
import org.futo.circles.feature.share.ShareableContent
import org.futo.circles.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.feature.timeline.data_source.ReadMessageDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.model.*
import org.matrix.android.sdk.api.util.Cancelable

class TimelineViewModel(
    private val roomNotificationsDataSource: RoomNotificationsDataSource,
    private val timelineDataSource: TimelineDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    accessLevelDataSource: AccessLevelDataSource,
    private val sendMessageDataSource: SendMessageDataSource,
    private val postOptionsDataSource: PostOptionsDataSource,
    private val userOptionsDataSource: UserOptionsDataSource,
    private val readMessageDataSource: ReadMessageDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val notificationsStateLiveData = roomNotificationsDataSource.notificationsStateLiveData
    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData
    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()
    val deleteCircleLiveData = SingleEventLiveData<Response<Unit?>>()


    fun sharePostContent(content: PostContent) {
        launchBg {
            postOptionsDataSource.getShareableContent(content)?.let { shareLiveData.postValue(it) }
        }
    }

    fun removeMessage(roomId: String, eventId: String) {
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun ignoreSender(senderId: String) {
        launchBg {
            ignoreUserLiveData.postValue(userOptionsDataSource.ignoreSender(senderId))
        }
    }

    fun saveToDevice(content: PostContent) {
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            saveToDeviceLiveData.postValue(Unit)
        }
    }

    fun sendPost(roomId: String, postContent: CreatePostContent, threadEventId: String?) {
        launchBg {
            when (postContent) {
                is MediaPostContent -> sendMessageDataSource.sendMedia(
                    roomId,
                    postContent.uri,
                    postContent.caption,
                    threadEventId,
                    postContent.mediaType
                )
                is TextPostContent -> sendMessageDataSource.sendTextMessage(
                    roomId, postContent.text, threadEventId
                )
            }
        }
    }

    fun editTextPost(eventId: String, roomId: String, newMessage: String) {
        sendMessageDataSource.editTextMessage(eventId, roomId, newMessage)
    }

    fun createPoll(roomId: String, pollContent: CreatePollContent) {
        sendMessageDataSource.createPoll(roomId, pollContent)
    }

    fun editPoll(roomId: String, eventId: String, pollContent: CreatePollContent) {
        sendMessageDataSource.editPoll(roomId, eventId, pollContent)
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        postOptionsDataSource.sendReaction(roomId, eventId, emoji)
    }

    fun unSendReaction(roomId: String, eventId: String, emoji: String) {
        launchBg {
            val result = postOptionsDataSource.unSendReaction(roomId, eventId, emoji)
            unSendReactionLiveData.postValue(result)
        }
    }

    fun leaveGroup() {
        launchBg { leaveGroupLiveData.postValue(leaveRoomDataSource.leaveGroup()) }
    }

    fun deleteGroup() {
        launchBg { deleteCircleLiveData.postValue(leaveRoomDataSource.deleteGroup()) }
    }

    fun deleteCircle() {
        launchBg { deleteCircleLiveData.postValue(leaveRoomDataSource.deleteCircle()) }
    }

    fun canLeaveRoom(): Boolean = leaveRoomDataSource.canLeaveRoom()

    fun pollVote(roomId: String, eventId: String, optionId: String) {
        postOptionsDataSource.pollVote(roomId, eventId, optionId)
    }

    fun endPoll(roomId: String, eventId: String) {
        postOptionsDataSource.endPoll(roomId, eventId)
    }

    fun markEventAsRead(positions: List<Int>) {
        val list = timelineEventsLiveData.value ?: return
        launchBg {
            positions.forEach { position ->
                list.getOrNull(position)
                    ?.let { readMessageDataSource.markAsRead(it.postInfo.roomId, it.id) }
            }
        }
    }

    override fun onCleared() {
        readMessageDataSource.setReadMarker()
        super.onCleared()
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        launchBg { roomNotificationsDataSource.setNotificationsEnabled(enabled) }
    }

}