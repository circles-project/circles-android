package org.futo.circles.gallery.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.RequestGalleryListItem
import org.futo.circles.core.room.invite.InviteRequestsDataSource
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    dataSource: PhotosDataSource,
    private val inviteRequestsDataSource: InviteRequestsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGalleriesFlow().asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.rejectInvite(roomId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun acceptPhotosInvite(roomId: String) {
        launchBg {
            val result = inviteRequestsDataSource.acceptInvite(roomId, CircleRoomTypeArg.Photo)
            inviteResultLiveData.postValue(result)
        }
    }

    fun inviteUser(room: RequestGalleryListItem) {
        launchBg {
            val result = inviteRequestsDataSource.inviteUser(room.id, room.requesterId)
            inviteResultLiveData.postValue(result)
        }
    }

    fun kickUser(room: RequestGalleryListItem) {
        launchBg { inviteRequestsDataSource.kickUser(room.id, room.requesterId) }
    }

}