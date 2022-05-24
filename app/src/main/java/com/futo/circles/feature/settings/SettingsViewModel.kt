package com.futo.circles.feature.settings

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg

class SettingsViewModel(
    private val dataSource: SettingsDataSource
) : ViewModel() {

    val profileLiveData = dataSource.profileLiveData
    val loadingLiveData = dataSource.loadingLiveData
    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()

    fun logOut() {
        launchBg { logOutLiveData.postValue(dataSource.logOut()) }
    }
}