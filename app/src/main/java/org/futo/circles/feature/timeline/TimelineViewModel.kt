package org.futo.circles.feature.timeline

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
import org.futo.circles.core.feature.room.RoomNotificationsDataSource
import org.futo.circles.core.feature.room.knoks.KnockRequestsDataSource
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.feature.user.UserOptionsDataSource
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.timeline.data_source.ReadMessageDataSource
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.util.Cancelable
import javax.inject.Inject

@HiltViewModel
class TimelineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    roomNotificationsDataSource: RoomNotificationsDataSource,
    timelineDataSourceFactory: BaseTimelineDataSource.Factory,
    accessLevelDataSource: AccessLevelDataSource,
    knockRequestsDataSource: KnockRequestsDataSource,
    private val sendMessageDataSource: SendMessageDataSource,
    private val postOptionsDataSource: PostOptionsDataSource,
    private val userOptionsDataSource: UserOptionsDataSource,
    private val readMessageDataSource: ReadMessageDataSource,
    circleFilterAccountDataManager: CircleFilterAccountDataManager
) : BaseTimelineViewModel(
    savedStateHandle,
    context,
    timelineDataSourceFactory.create(savedStateHandle.get<String>("timelineId") != null),
    circleFilterAccountDataManager
) {

    val session = MatrixSessionProvider.currentSession
    val profileLiveData = session?.userService()?.getUserLive(session.myUserId)
    val notificationsStateLiveData = roomNotificationsDataSource.notificationsStateLiveData
    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()
    val knockRequestCountLiveData =
        knockRequestsDataSource.getKnockRequestCountLiveDataForCurrentUserInRoom(
            timelineId ?: roomId
        )


    fun sharePostContent(content: PostContent) {
        launchBg {
            postOptionsDataSource.getShareableContent(content)?.let { shareLiveData.postValue(it) }
        }
    }

    fun removeMessage(roomId: String, eventId: String) {
        launchBg { postOptionsDataSource.removeMessage(roomId, eventId) }
    }

    fun ignoreSender(senderId: String) {
        launchBg {
            val result = userOptionsDataSource.ignoreSender(senderId)
            ignoreUserLiveData.postValue(result)
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

    fun editPost(eventId: String, roomId: String, postContent: CreatePostContent) {
        when (postContent) {
            is MediaPostContent -> postContent.caption?.let {
                sendMessageDataSource.editMediaCaption(eventId, roomId, postContent.caption)
            }

            is TextPostContent -> sendMessageDataSource.editTextMessage(
                eventId, roomId, postContent.text
            )
        }
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


    fun pollVote(roomId: String, eventId: String, optionId: String) {
        postOptionsDataSource.pollVote(roomId, eventId, optionId)
    }

    fun endPoll(roomId: String, eventId: String) {
        postOptionsDataSource.endPoll(roomId, eventId)
    }

    fun markTimelineAsRead(roomId: String, isGroup: Boolean) {
        launchBg {
            if (isGroup) readMessageDataSource.markRoomAsRead(roomId)
            else session?.getRoom(roomId)?.roomSummary()?.spaceChildren?.map {
                async { readMessageDataSource.markRoomAsRead(it.childRoomId) }
            }?.awaitAll()
        }
    }
}