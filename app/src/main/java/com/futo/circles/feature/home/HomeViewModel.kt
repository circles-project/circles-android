package com.futo.circles.feature.home

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.home.data_source.HomeDataSource

class HomeViewModel(
    private val dataSource: HomeDataSource
) : ViewModel() {

    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()

    fun logOut() {
        launchBg { logOutLiveData.postValue(dataSource.logOut()) }
    }
}