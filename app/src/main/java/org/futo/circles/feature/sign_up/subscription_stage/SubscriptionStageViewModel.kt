package org.futo.circles.feature.sign_up.subscription_stage

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SubscriptionStageViewModel(
    private val dataSource: SubscriptionStageDataSource
) : ViewModel() {

    val subscribeLiveData = SingleEventLiveData<Response<RegistrationResult>>()

    fun validateSubscriptionReceipt(receipt: String) {
        launchBg {
            subscribeLiveData.postValue(dataSource.validateSubscriptionReceipt(receipt))
        }
    }

}