package org.futo.circles.feature.timeline.poll

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import org.futo.circles.core.mapping.toPollContent
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.CreatePostContent
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.send.SendState
import javax.inject.Inject

@HiltViewModel
class CreatePollViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendMessageDataSource: SendMessageDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String? = savedStateHandle["eventId"]
    val pollToEditLiveData = MutableLiveData<PollContent>()
    val sendLiveData = MutableLiveData<LiveData<SendState>>()

    init {
        setEditPostInfo()
    }

    fun onSendPoll(pollContent: CreatePollContent) {
        val newEventId = eventId?.let {
            sendMessageDataSource.editPoll(roomId, it, pollContent)
            it
        } ?: sendMessageDataSource.createPoll(roomId, pollContent)

        val sendStateLiveData =
            MatrixSessionProvider.currentSession?.getRoom(roomId)?.timelineService()
                ?.getTimelineEventLive(newEventId)
                ?.map { it.getOrNull()?.root?.sendState ?: SendState.SENDING }
                ?: MutableLiveData(SendState.SENDING)
        sendLiveData.postValue(sendStateLiveData)
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val session = MatrixSessionProvider.currentSession
        val room = session?.getRoom(roomId) ?: return
        val event = room.getTimelineEvent(eventId)?.toPollContent() ?: return
        pollToEditLiveData.postValue(event)
    }
}