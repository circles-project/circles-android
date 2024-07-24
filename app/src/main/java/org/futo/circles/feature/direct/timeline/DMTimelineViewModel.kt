package org.futo.circles.feature.direct.timeline

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.model.MediaType
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
class DMTimelineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    timelineDataSourceFactory: BaseTimelineDataSource.Factory,
    private val postOptionsDataSource: PostOptionsDataSource,
    private val readMessageDataSource: ReadMessageDataSource,
    circleFilterAccountDataManager: CircleFilterAccountDataManager,
    private val sendMessageDataSource: SendMessageDataSource
) : BaseTimelineViewModel(
    savedStateHandle,
    context,
    timelineDataSourceFactory.create(false),
    circleFilterAccountDataManager
) {

    val session = MatrixSessionProvider.currentSession
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()

    fun sharePostContent(content: PostContent) {
        launchBg {
            postOptionsDataSource.getShareableContent(content, null)
                ?.let { shareLiveData.postValue(it) }
        }
    }

    fun removeMessage(roomId: String, eventId: String) {
        launchBg { postOptionsDataSource.removeMessage(roomId, eventId) }
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

    fun markTimelineAsRead(roomId: String, isGroup: Boolean) {
        launchBg {
            if (isGroup) readMessageDataSource.markRoomAsRead(roomId)
            else session?.getRoom(roomId)?.roomSummary()?.spaceChildren?.map {
                async { readMessageDataSource.markRoomAsRead(it.childRoomId) }
            }?.awaitAll()
        }
    }

    fun sendTextMessageDm(message: String) {
        launchBg { sendMessage(roomId, TextPostContent(message)) }
    }

    fun sendMediaDm(uri: Uri, mediaType: MediaType) {
        launchBg { sendMessage(roomId, MediaPostContent(null, uri, mediaType)) }
    }

    private suspend fun sendMessage(
        roomId: String,
        postContent: CreatePostContent
    ): String = when (postContent) {
        is MediaPostContent -> sendMessageDataSource.sendMedia(
            roomId,
            postContent.uri,
            null,
            null,
            postContent.mediaType
        ).first

        is TextPostContent -> sendMessageDataSource.sendTextMessage(
            roomId, postContent.text, null
        )
    }

    fun editTextMessage(eventId: String, roomId: String, message: String) {
        sendMessageDataSource.editTextMessage(eventId, roomId, message)
    }
}