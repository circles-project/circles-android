package org.futo.circles.feature.circles

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.CircleListItem

class CirclesViewModel(private val dataSource: CirclesDataSource) : ViewModel() {

    val roomsLiveData = dataSource.getCirclesLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }

    fun inviteUser(room: CircleListItem) {

    }

    fun kickUser(room: CircleListItem) {

    }

}