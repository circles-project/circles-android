package org.futo.circles.feature.timeline.post.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.timeline.post.PostContentDataSource
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.send.SendState
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postContentDataSource: PostContentDataSource,
    private val sendMessageDataSource: SendMessageDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String? = savedStateHandle["eventId"]
    private val isEdit: Boolean = savedStateHandle.getOrThrow("isEdit")

    val postToEditContentLiveData = MutableLiveData<PostContent>()
    val sendEventObserverLiveData = MutableLiveData<Pair<String, LiveData<SendState>>>()

    init {
        if (isEdit) setEditPostInfo()
    }

    fun onSendAction(content: CreatePostContent) {
        launchBg {
            val newEventId = if (isEdit) {
                eventId?.let { editPost(it, roomId, content) }
                eventId ?: ""
            } else sendPost(roomId, content, eventId)

            val sendStateLiveData =
                MatrixSessionProvider.currentSession?.getRoom(roomId)?.timelineService()
                    ?.getTimelineEventLive(newEventId)
                    ?.map { it.getOrNull()?.root?.sendState ?: SendState.SENDING }
                    ?: MutableLiveData(SendState.SENDING)
            sendEventObserverLiveData.postValue(newEventId to sendStateLiveData)
        }
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val content = postContentDataSource.getPostContent(roomId, eventId) ?: return
        postToEditContentLiveData.value = content
    }

    private suspend fun sendPost(
        roomId: String,
        postContent: CreatePostContent,
        threadEventId: String?
    ): String = when (postContent) {
        is MediaPostContent -> sendMessageDataSource.sendMedia(
            roomId,
            postContent.uri,
            postContent.caption,
            threadEventId,
            postContent.mediaType
        ).first

        is TextPostContent -> sendMessageDataSource.sendTextMessage(
            roomId, postContent.text, threadEventId
        )
    }

    private fun editPost(eventId: String, roomId: String, postContent: CreatePostContent) {
        when (postContent) {
            is MediaPostContent -> postContent.caption?.let {
                sendMessageDataSource.editMediaCaption(eventId, roomId, postContent.caption)
            }

            is TextPostContent -> sendMessageDataSource.editTextMessage(
                eventId, roomId, postContent.text
            )
        }
    }
}