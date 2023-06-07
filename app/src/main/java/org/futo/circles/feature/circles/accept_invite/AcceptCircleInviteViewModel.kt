package org.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.SelectableRoomListItem
import javax.inject.Inject

@HiltViewModel
class AcceptCircleInviteViewModel @Inject constructor(
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