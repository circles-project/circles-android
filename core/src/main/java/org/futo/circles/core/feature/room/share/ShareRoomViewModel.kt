package org.futo.circles.core.feature.room.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.buildShareProfileUrl
import org.futo.circles.core.base.buildShareRoomUrl
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class ShareRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val isProfile: Boolean = savedStateHandle.getOrThrow("isProfile")

    val roomLiveData =
        MatrixSessionProvider.currentSession?.roomService()?.getRoomSummaryLive(roomId)

    fun buildInviteUrl(): String =
        if (isProfile) buildShareProfileUrl(roomId)
        else {
            val summary =
                MatrixSessionProvider.currentSession?.getRoom(roomId)?.roomSummary()
            summary?.let { buildShareRoomUrl(roomId, summary.nameOrId(), summary.topic) } ?: ""
        }
}