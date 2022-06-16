package org.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.SelectableRoomListItem

class AcceptCircleInviteViewModel(
    private val dataSource: AcceptCircleInviteDataSource
) : ViewModel() {

    val circlesLiveData = dataSource.circlesLiveData
    val acceptResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun getSelectedCircles() = dataSource.getSelectedCircles()

    fun acceptInvite() {
        launchBg { acceptResultLiveData.postValue(dataSource.acceptCircleInvite()) }
    }

    fun onCircleSelected(item: SelectableRoomListItem) {
        dataSource.toggleCircleSelect(item)
    }
}