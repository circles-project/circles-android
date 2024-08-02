package org.futo.circles.feature.timeline

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.toRoomInfo
import org.futo.circles.core.feature.room.RoomNotificationsDataSource
import org.futo.circles.core.feature.room.requests.KnockRequestsDataSource
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.feature.user.UserOptionsDataSource
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TimelineTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.timeline.data_source.ReadMessageDataSource
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
    private val postOptionsDataSource: PostOptionsDataSource,
    private val userOptionsDataSource: UserOptionsDataSource,
    private val readMessageDataSource: ReadMessageDataSource,
) : BaseTimelineViewModel(
    context,
    //TODO type here
    timelineDataSourceFactory.create(
        TimelineTypeArg.GROUP, savedStateHandle["roomId"], savedStateHandle["threadEventId"]
    ),
) {

    private val roomId: String? = savedStateHandle["roomId"]

    val session = MatrixSessionProvider.getSessionOrThrow()

    val titleLiveData = roomId?.let {
        session.getRoom(roomId)?.getRoomSummaryLive()?.map {
            it.getOrNull()?.toRoomInfo()?.title ?: ""
        }
    } ?: MutableLiveData(context.getString(org.futo.circles.core.R.string.all_posts))


    val profileLiveData = session.userService().getUserLive(session.myUserId)
    val notificationsStateLiveData = roomNotificationsDataSource.notificationsStateLiveData
    val accessLevelLiveData = accessLevelDataSource.accessLevelFlow.asLiveData()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()
    val knockRequestCountLiveData = roomId?.let {
        knockRequestsDataSource.getKnockRequestCountFlow(roomId).asLiveData()
    } ?: MutableLiveData(0)


    fun sharePostContent(content: PostContent, view: View) {
        launchBg {
            postOptionsDataSource.getShareableContent(content, view)
                ?.let { shareLiveData.postValue(it) }
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

    //TODO
    fun markTimelineAsRead(roomId: String) {
        launchBg { readMessageDataSource.markRoomAsRead(roomId) }
    }
}