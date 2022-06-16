package org.futo.circles.feature.settings.active_sessions.remove_session

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

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