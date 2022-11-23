package org.futo.circles.feature.timeline.post.info

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.Room
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class PostInfoViewModel(
    private val roomId: String,
    private val eventId: String
) : ViewModel() {

    val infoLiveData = SingleEventLiveData<String>()

    init {
        updateInfo()
    }

    private fun updateInfo() {
        val session = MatrixSessionProvider.currentSession ?: return
        val room = session.getRoom(roomId) ?: return
        val info = "${getRoomInfo(room)}\n${getEventInfo(room)}"
        infoLiveData.postValue(info)
    }

    private fun getRoomInfo(room: Room): String {
        val roomSummary = room.roomSummary()?.toString()
        return "Room:\n$roomSummary"
    }

    private fun getEventInfo(room: Room): String {
        val event = room.getTimelineEvent(eventId)?.root?.toString() ?: return ""
        return "Event:\n$event"
    }
}