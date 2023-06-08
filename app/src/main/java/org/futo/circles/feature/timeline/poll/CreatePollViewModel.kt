package org.futo.circles.feature.timeline.poll

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.mapping.toPollContent
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import javax.inject.Inject

@HiltViewModel
class CreatePollViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String? = savedStateHandle["eventId"]
    val pollToEditLiveData = MutableLiveData<PollContent>()

    init {
        setEditPostInfo()
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val session = MatrixSessionProvider.currentSession
        val room = session?.getRoom(roomId) ?: return
        val event = room.getTimelineEvent(eventId)?.toPollContent() ?: return
        pollToEditLiveData.postValue(event)
    }
}