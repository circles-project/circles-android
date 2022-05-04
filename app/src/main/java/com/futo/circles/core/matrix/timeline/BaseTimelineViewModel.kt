package com.futo.circles.core.matrix.timeline

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.timeline.data_source.BaseTimelineDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.share.ShareableContent
import com.futo.circles.model.ImageContent
import com.futo.circles.model.PostContent
import org.matrix.android.sdk.api.util.Cancelable

abstract class BaseTimelineViewModel(
    private val dataSource: BaseTimelineDataSource
) : ViewModel() {

    val titleLiveData = dataSource.roomTitleLiveData
    val timelineEventsLiveData = dataSource.timelineEventsLiveData
    val accessLevelLiveData = dataSource.accessLevelFlow.asLiveData()
    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadImageLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()

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

    fun removeMessage(eventId: String) {
        dataSource.removeMessage(eventId)
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

    fun sendTextPost(message: String, threadEventId: String?) {
        dataSource.sendTextMessage(message, threadEventId)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

    fun sendImagePost(uri: Uri, threadEventId: String?) {
        dataSource.sendImage(uri, threadEventId)
        if (threadEventId == null) scrollToTopLiveData.postValue(Unit)
    }

    fun sendReaction(eventId: String, emoji: String) {
        dataSource.sendReaction(eventId, emoji)
    }

    fun unSendReaction(eventId: String, emoji: String) {
        launchBg {
            unSendReactionLiveData.postValue(dataSource.unSendReaction(eventId, emoji))
        }
    }
}