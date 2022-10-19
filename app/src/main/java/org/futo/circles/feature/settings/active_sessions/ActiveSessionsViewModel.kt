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
    val startReAuthEventLiveData = dataSource.startReAuthEventLiveData

    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun verifySession(deviceId: String) {
        launchBg {
            val verifyResult = dataSource.verifyDevice(deviceId)
            verifySessionLiveData.postValue(verifyResult)
        }
    }

    fun removeSession(deviceId: String) {
        launchBg {
            val deactivateResult = dataSource.removeSession(deviceId)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }

    fun enableCrossSigning() {
        launchBg {
            val crossSigningResult = dataSource.enableCrossSigning()
            removeSessionLiveData.postValue(crossSigningResult)
        }
    }
}