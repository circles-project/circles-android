package com.futo.circles.feature.group_timeline

import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.timeline.BaseTimelineViewModel
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource

class GroupTimelineViewModel(
    private val dataSource: GroupTimelineDatasource
) : BaseTimelineViewModel(dataSource) {

    val leaveGroupLiveData = SingleEventLiveData<Response<Unit?>>()

    fun leaveGroup() {
        launchBg { leaveGroupLiveData.postValue(dataSource.leaveGroup()) }
    }

}