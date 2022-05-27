package com.futo.circles.feature.settings.active_sessions.remove_session

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg

class RemoveSessionViewModel(
    private val dataSource: RemoveSessionDataSource
) : ViewModel() {

    val removeSessionLiveData = SingleEventLiveData<Response<Unit?>>()

    fun removeSession(password: String) {
        launchBg {
            val deactivateResult = dataSource.removeSession(password)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }

}