package com.futo.circles.feature.settings.deactivate

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg

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