package org.futo.circles.gallery.feature.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.room.leave.LeaveRoomDataSource
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@HiltViewModel
class GalleryDialogFragmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val leaveRoomDataSource: LeaveRoomDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)
    val titleLiveData = room?.getRoomSummaryLive()?.map { it.getOrNull()?.nameOrId() }

    val deleteGalleryLiveData = SingleEventLiveData<Response<Unit?>>()

    fun deleteGallery() {
        launchBg { deleteGalleryLiveData.postValue(leaveRoomDataSource.deleteGallery()) }
    }
}