package com.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.circles.accept_invite.data_source.AcceptCircleInviteDataSource
import com.futo.circles.model.SelectableRoomListItem

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