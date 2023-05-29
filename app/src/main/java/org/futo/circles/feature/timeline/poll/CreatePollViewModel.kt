package org.futo.circles.feature.timeline.poll

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.mapping.toPollContent
import org.futo.circles.model.PollContent
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class CreatePollViewModel(
    private val roomId: String,
    private val eventId: String?
) : ViewModel() {

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