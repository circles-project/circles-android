package org.futo.circles.feature.room.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.base.CIRCULI_INVITE_URL
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.ShareableContent
import org.futo.circles.core.model.TextShareable
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

@HiltViewModel
class ShareRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    val roomLiveData =
        MatrixSessionProvider.currentSession?.roomService()?.getRoomSummaryLive(roomId)


    fun createSharableUrl(): ShareableContent = TextShareable(buildInviteUrl())

    private fun buildInviteUrl(): String = CIRCULI_INVITE_URL + roomId
}