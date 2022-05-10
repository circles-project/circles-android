package com.futo.circles.feature.following

import androidx.lifecycle.ViewModel
import com.futo.circles.feature.following.data_source.FollowingDataSource

class FollowingViewModel(
    dataSource: FollowingDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.roomsLiveData
}