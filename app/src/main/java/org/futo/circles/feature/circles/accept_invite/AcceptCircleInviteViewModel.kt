package org.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.SelectableRoomListItem

class AcceptCircleInviteViewModel(
    private val acceptInviteDataSource: AcceptCircleInviteDataSource
) : ViewModel() {

    val acceptResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun acceptInvite(selectedCircles: List<SelectableRoomListItem>) {
        launchBg {
            acceptResultLiveData.postValue(
                acceptInviteDataSource.acceptCircleInvite(selectedCircles)
            )
        }
    }
}