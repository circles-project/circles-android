package org.futo.circles.core.feature.room.share

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.share.buildShareRoomUrl
import org.futo.circles.core.model.ShareUrlTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

@HiltViewModel
class ShareRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val shareUrlType: ShareUrlTypeArg = savedStateHandle.getOrThrow("urlType")

    val roomLiveData =
        MatrixSessionProvider.currentSession?.roomService()?.getRoomSummaryLive(roomId)

    fun buildInviteUrl(): String = buildShareRoomUrl(shareUrlType, roomId)
}