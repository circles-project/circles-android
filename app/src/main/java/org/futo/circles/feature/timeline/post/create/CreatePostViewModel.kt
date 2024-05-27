package org.futo.circles.feature.timeline.post.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.util.Optional
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

    init {
        if (isEdit) setEditPostInfo()
    }

    fun onSendAction(content: CreatePostContent): LiveData<Optional<TimelineEvent>>? {
        val eventId = if (isEdit) {
            eventId?.let { editPost(it, roomId, content) }
            eventId ?: ""
        } else sendPost(roomId, content, eventId)

        return MatrixSessionProvider.currentSession?.getRoom(roomId)?.timelineService()
            ?.getTimelineEventLive(eventId)
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val content = postContentDataSource.getPostContent(roomId, eventId) ?: return
        postToEditContentLiveData.value = content
    }

    private fun sendPost(
        roomId: String,
        postContent: CreatePostContent,
        threadEventId: String?
    ): String {
        var eventId = ""
        launchBg {
            eventId = when (postContent) {
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
        }
        return eventId
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