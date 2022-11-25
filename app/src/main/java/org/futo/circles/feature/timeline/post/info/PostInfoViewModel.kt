package org.futo.circles.feature.timeline.post.info

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.provider.MatrixSessionProvider
import org.json.JSONObject
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
        val info = "${getEventInfo(room)}\n\n\n${getRoomInfo(room)}"
        infoLiveData.postValue(info)
    }

    private fun getRoomInfo(room: Room): String {
        val roomSummary = room.roomSummary() ?: return ""
        val formattedJson = JSONObject(Gson().toJson(roomSummary)).toString(4)
        return "Room:\n$formattedJson"
    }

    private fun getEventInfo(room: Room): String {
        val event = room.getTimelineEvent(eventId) ?: return ""
        val formattedEventJson = JSONObject(Gson().toJson(event)).toString(4)
        val formattedClearContentJson =
            JSONObject(Gson().toJson(event.root.getClearContent())).toString(4)
        return "Event:\n$formattedEventJson\nContent:$formattedClearContentJson"
    }
}