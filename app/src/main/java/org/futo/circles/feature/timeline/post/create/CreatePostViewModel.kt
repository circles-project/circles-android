package org.futo.circles.feature.timeline.post.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String? = savedStateHandle["eventId"]
    private val isEdit: Boolean = savedStateHandle.getOrThrow("isEdit")

    val textToEditLiveData = MutableLiveData<String>()

    init {
        if (isEdit) setEditPostInfo()
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val session = MatrixSessionProvider.currentSession
        val room = session?.getRoom(roomId) ?: return
        val event = room.getTimelineEvent(eventId) ?: return
        textToEditLiveData.postValue(event.getTextEditableContent(false))
    }
}