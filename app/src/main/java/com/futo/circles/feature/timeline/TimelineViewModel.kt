package com.futo.circles.feature.timeline

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.timeline.post.share.ShareableContent
import com.futo.circles.feature.timeline.data_source.TimelineDataSource
import com.futo.circles.model.ImageContent
import com.futo.circles.model.PostContent
import org.matrix.android.sdk.api.util.Cancelable

class TimelineViewModel(
    private val dataSource: TimelineDataSource
) : ViewModel() {

    val titleLiveData = dataSource.roomTitleLiveData
    val timelineEventsLiveData = dataSource.timelineEventsLiveData
    val accessLevelLiveData = dataSource.accessLevelFlow.asLiveData()
    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadImageLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()
    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()
    val deleteCircleLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        dataSource.startTimeline()
    }

    override fun onCleared() {
        dataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore() {
        dataSource.loadMore()
    }

    fun toggleRepliesVisibilityFor(eventId: String) {
        dataSource.toggleRepliesVisibility(eventId)
    }

    fun sharePostContent(content: PostContent) {
        launchBg {
            shareLiveData.postValue(dataSource.getShareableContent(content))
        }
    }

    fun removeMessage(roomId: String, eventId: String) {
        dataSource.removeMessage(roomId, eventId)
    }

    fun ignoreSender(senderId: String) {
        launchBg {
            ignoreUserLiveData.postValue(dataSource.ignoreSender(senderId))
        }
    }

    fun saveImage(imageContent: ImageContent) {
        launchBg {
            dataSource.saveImage(imageContent)
            downloadImageLiveData.postValue(Unit)
        }
    }

    fun sendTextPost(roomId: String, message: String, threadEventId: String?) {
        dataSource.sendTextMessage(roomId, message, threadEventId)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

    fun sendImagePost(roomId: String, uri: Uri, threadEventId: String?) {
        dataSource.sendImage(roomId, uri, threadEventId)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

    fun sendReaction(roomId: String, eventId: String, emoji: String) {
        dataSource.sendReaction(roomId, eventId, emoji)
    }

    fun unSendReaction(roomId: String, eventId: String, emoji: String) {
        launchBg {
            val result = dataSource.unSendReaction(roomId, eventId, emoji)
            unSendReactionLiveData.postValue(result)
        }
    }

    fun leaveGroup() {
        launchBg { leaveGroupLiveData.postValue(dataSource.leaveGroup()) }
    }

    fun deleteCircle() {
        launchBg { deleteCircleLiveData.postValue(dataSource.deleteCircle()) }
    }

    fun isSingleOwner(): Boolean = dataSource.isUserSingleRoomOwner()

}