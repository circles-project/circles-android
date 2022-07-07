package org.futo.circles.feature.settings.active_sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class ActiveSessionsViewModel(
    private val dataSource: ActiveSessionsDataSource
) : ViewModel() {

    val activeSessionsLiveData = dataSource.getActiveSessionsFlow().asLiveData()
    val removeSessionLiveData = SingleEventLiveData<Response<Unit?>>()

    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun verifySession(deviceId: String) {
        dataSource.verifyDevice(deviceId)
    }

    fun removeSession(deviceId: String, password: String) {
        launchBg {
            val deactivateResult = dataSource.removeSession(deviceId, password)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }
}