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
    val enableCrossSigningLiveData = SingleEventLiveData<Response<Unit?>>()
    val verifySessionLiveData = SingleEventLiveData<Response<Unit?>>()

    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun verifySession(deviceId: String) {
        launchBg {
            val verifyResult = dataSource.verifyDevice(deviceId)
            verifySessionLiveData.postValue(verifyResult)
        }
    }

    fun removeSession(deviceId: String, password: String) {
        launchBg {
            val deactivateResult = dataSource.removeSession(deviceId, password)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }

    fun enableCrossSigning(password: String) {
        launchBg {
            val crossSigningResult = dataSource.enableCrossSigning(password)
            removeSessionLiveData.postValue(crossSigningResult)
        }
    }
}