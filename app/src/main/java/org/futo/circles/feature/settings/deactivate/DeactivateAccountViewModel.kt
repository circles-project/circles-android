package org.futo.circles.feature.settings.deactivate

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class DeactivateAccountViewModel(
    private val dataSource: DeactivateAccountDataSource
) : ViewModel() {

    val deactivateLiveData = SingleEventLiveData<Response<Unit?>>()

    fun deactivateAccount(password: String) {
        launchBg {
            val deactivateResult = dataSource.deactivateAccount(password)
            deactivateLiveData.postValue(deactivateResult)
        }
    }

}