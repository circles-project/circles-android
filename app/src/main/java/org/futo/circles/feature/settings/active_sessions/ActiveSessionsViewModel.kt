package org.futo.circles.feature.settings.active_sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class ActiveSessionsViewModel(
    private val dataSource: ActiveSessionsDataSource
) : ViewModel() {

    val activeSessionsLiveData = dataSource.getActiveSessionsFlow().asLiveData()
    val removeSessionLiveData = SingleEventLiveData<Response<Unit?>>()
    val resetKeysLiveData = SingleEventLiveData<Response<Unit?>>()
    val startReAuthEventLiveData = dataSource.startReAuthEventLiveData

    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun removeSession(deviceId: String) {
        launchBg {
            val deactivateResult = dataSource.removeSession(deviceId)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }

    fun resetKeysToEnableCrossSigning() {
        launchBg {
            val resetKeysResult = dataSource.resetKeysToEnableCrossSigning()
            resetKeysLiveData.postValue(resetKeysResult)
        }
    }
}