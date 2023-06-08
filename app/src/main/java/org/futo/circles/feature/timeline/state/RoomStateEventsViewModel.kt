package org.futo.circles.feature.timeline.state

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.json.JSONObject
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.Event
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class RoomStateEventsViewModel @Inject constructor(savedStateHandle: SavedStateHandle) :
    ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    var stateEventsLiveData = MatrixSessionProvider.currentSession?.getRoom(roomId)?.stateService()
        ?.getStateEventsLive(emptySet(), QueryStringValue.IsNotNull)?.map {
            var text = ""
            it.forEach { event ->
                text += getEventInfo(event)
                text += "\n\n-----------------------------------------------------\n\n"
            }
            text
        }


    private fun getEventInfo(event: Event): String {
        val formattedEventJson = JSONObject(Gson().toJson(event)).toString(4)
        val formattedClearContentJson =
            JSONObject(Gson().toJson(event.getClearContent())).toString(4)
        return "Event:\n$formattedEventJson\nContent:$formattedClearContentJson"
    }
}