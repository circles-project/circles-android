package org.futo.circles.feature.direct.timeline

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.timeline.BaseTimelineViewModel
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.core.model.TimelineTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.DateUtils
import org.futo.circles.feature.timeline.data_source.ReadMessageDataSource
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.DmShapeType
import org.futo.circles.model.DmShapeType.First
import org.futo.circles.model.DmShapeType.Last
import org.futo.circles.model.DmShapeType.Middle
import org.futo.circles.model.DmShapeType.Single
import org.futo.circles.model.DmTimelineListItem
import org.futo.circles.model.DmTimelineLoadingItem
import org.futo.circles.model.DmTimelineMessage
import org.futo.circles.model.DmTimelineTimeHeaderItem
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.futo.circles.model.toDmTimelineMessage
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.util.Cancelable
import javax.inject.Inject

@HiltViewModel
class DMTimelineViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context,
    timelineDataSourceFactory: BaseTimelineDataSource.Factory,
    private val postOptionsDataSource: PostOptionsDataSource,
    private val readMessageDataSource: ReadMessageDataSource,
    private val sendMessageDataSource: SendMessageDataSource
) : BaseTimelineViewModel(
    context,
    timelineDataSourceFactory.create(
        TimelineTypeArg.DM, savedStateHandle["roomId"], null
    )
) {

    val dmTimelineEventsLiveData: LiveData<List<DmTimelineListItem>> =
        timelineEventsLiveData.map { transformToDmMessages(it) }

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    val session = MatrixSessionProvider.getSessionOrThrow()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val saveToDeviceLiveData = SingleEventLiveData<Unit>()
    val unSendReactionLiveData = SingleEventLiveData<Response<Cancelable?>>()

    val userTitleLiveData = session.getRoom(roomId)?.getRoomSummaryLive()?.map {
        it.getOrNull()?.let { roomSummary ->
            session.getUserOrDefault(roomSummary.directUserId ?: "").toCirclesUserSummary()
        }
    }

    fun sharePostContent(content: PostContent) {
        launchBg {
            postOptionsDataSource.getShareableContent(content)
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

    fun markTimelineAsRead(roomId: String) {
        launchBg { readMessageDataSource.markRoomAsRead(roomId) }
    }

    fun sendTextMessageDm(message: String, onSent: () -> Unit) {
        launchBg {
            sendMessage(roomId, TextPostContent(message))
            withContext(Dispatchers.Main) { onSent() }
        }
    }

    fun sendMediaDm(uri: Uri, mediaType: MediaType, onSent: () -> Unit) {
        launchBg {
            sendMessage(roomId, MediaPostContent(null, uri, mediaType))
            withContext(Dispatchers.Main) { onSent() }
        }
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

    private fun transformToDmMessages(items: List<PostListItem>): List<DmTimelineListItem> {
        val listWithDates = setupWithDatesListItems(items)
        return listWithDates.mapIndexed { i, item ->
            if (item is DmTimelineMessage) item.copy(
                shapeType = getDmShapeType(
                    item,
                    listWithDates.getOrNull(i - 1) as? DmTimelineMessage,
                    listWithDates.getOrNull(i + 1) as? DmTimelineMessage
                )
            )
            else item
        }
    }

    private fun setupWithDatesListItems(items: List<PostListItem>): List<DmTimelineListItem> {
        val listWithDates = mutableListOf<DmTimelineListItem>()
        var currentGroupTime = 0L
        items.map {
            when (it) {
                is Post -> {
                    val postTime = it.postInfo.timestamp
                    val isSameDay = DateUtils.isSameDay(currentGroupTime, postTime)
                    if (!isSameDay) {
                        currentGroupTime = postTime
                        listWithDates.add(DmTimelineTimeHeaderItem(postTime))
                    }
                    listWithDates.add(it.toDmTimelineMessage())
                }

                is TimelineLoadingItem -> listWithDates.add(DmTimelineLoadingItem())
            }
        }

        return listWithDates
    }

    private fun getDmShapeType(
        item: DmTimelineMessage, previousItem: DmTimelineMessage?,
        nextItem: DmTimelineMessage?
    ): DmShapeType {
        val currentItemSenderId = item.info.sender.userId
        val previousSenderId = previousItem?.info?.sender?.userId
        val nextSenderId = nextItem?.info?.sender?.userId

        return if (previousSenderId == currentItemSenderId && nextSenderId == currentItemSenderId) Middle
        else if (previousSenderId == currentItemSenderId) Last
        else if (nextSenderId == currentItemSenderId) First
        else Single
    }
}