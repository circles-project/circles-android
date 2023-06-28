package org.futo.circles.feature.room.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.base.SHARE_PROFILE_URL_PREFIX
import org.futo.circles.base.SHARE_ROOM_URL_PREFIX
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

@HiltViewModel
class ShareRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val isProfile: Boolean = savedStateHandle.getOrThrow("isProfile")

    val roomLiveData =
        MatrixSessionProvider.currentSession?.roomService()?.getRoomSummaryLive(roomId)

    fun buildInviteUrl(): String = if (isProfile)
        SHARE_PROFILE_URL_PREFIX + MatrixSessionProvider.currentSession?.myUserId + "/" + roomId
    else SHARE_ROOM_URL_PREFIX + roomId
}