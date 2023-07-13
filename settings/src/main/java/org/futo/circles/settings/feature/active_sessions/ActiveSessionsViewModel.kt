package org.futo.circles.settings.feature.active_sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ActiveSessionsViewModel @Inject constructor(
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