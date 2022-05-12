package com.futo.circles.feature.circles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.circles.data_source.CirclesDataSource
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CirclesViewModel(
    private val dataSource: CirclesDataSource
) : ViewModel() {

    val circlesLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams {
            excludeType = null
        })?.map { list -> dataSource.filterCircles(list) }

    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun rejectInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.rejectInvite(roomId)) }
    }
}