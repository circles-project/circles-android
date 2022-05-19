package com.futo.circles.feature.profile

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.profile.data_source.ProfileDataSource

class ProfileViewModel(
    private val dataSource: ProfileDataSource
) : ViewModel() {

    val profileLiveData = dataSource.profileLiveData
    val logOutLiveData = SingleEventLiveData<Response<Unit?>>()

    fun logOut() {
        launchBg { logOutLiveData.postValue(dataSource.logOut()) }
    }
}