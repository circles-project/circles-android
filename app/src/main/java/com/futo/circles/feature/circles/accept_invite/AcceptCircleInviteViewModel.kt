package com.futo.circles.feature.circles.accept_invite

import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.circles.accept_invite.data_source.AcceptCircleInviteDataSource
import com.futo.circles.model.SelectableRoomListItem

class AcceptCircleInviteViewModel(
    private val dataSource: AcceptCircleInviteDataSource
) : ViewModel() {

    val circlesLiveData = dataSource.circlesLiveData

    fun getSelectedCircles() = dataSource.getSelectedCircles()

    fun acceptInvite() {
        launchBg { dataSource.acceptCircleInvite() }
    }

    fun onCircleSelected(item: SelectableRoomListItem) {
        dataSource.toggleCircleSelect(item)
    }
}