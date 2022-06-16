package org.futo.circles.feature.settings

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

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