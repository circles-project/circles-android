package org.futo.circles.feature.timeline

import androidx.lifecycle.asLiveData
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.people.UserOptionsDataSource
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.feature.timeline.post.share.ShareableContent
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.PostContent
import org.futo.circles.model.TextPostContent
import org.matrix.android.sdk.api.util.Cancelable

class TimelineViewModel(
    private val timelineDataSource: TimelineDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource,
    private val sendMessageDataSource: SendMessageDataSource,
    private val postOptionsDataSource: PostOptionsDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val timelineEventsLiveData = timelineDataSource.timelineEventsLiveData
    val accessLevelLiveData = timelineDataSource.accessLevelFlow.asLiveData()
    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()
    val deleteCircleLiveData = SingleEventLiveData<Response<Unit?>>()


    fun toggleRepliesVisibilityFor(eventId: String) {
        timelineDataSource.toggleRepliesVisibility(eventId)
    }

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
        when (postContent) {
            is MediaPostContent -> sendMessageDataSource.sendMedia(
                roomId, postContent.uri, threadEventId, postContent.mediaType
            )
            is TextPostContent -> sendMessageDataSource.sendTextMessage(
                roomId, postContent.text, threadEventId
            )
        }
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
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

    fun deleteCircle() {
        launchBg { deleteCircleLiveData.postValue(leaveRoomDataSource.deleteCircle()) }
    }

    fun isSingleOwner(): Boolean = leaveRoomDataSource.isUserSingleRoomOwner()

}