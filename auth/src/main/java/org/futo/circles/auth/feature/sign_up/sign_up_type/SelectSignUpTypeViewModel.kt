package org.futo.circles.auth.feature.sign_up.sign_up_type

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.auth.subscriptions.SubscriptionManager

class SelectSignUpTypeViewModel(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = org.futo.circles.core.SingleEventLiveData<Response<Unit?>>()
    val isSubscribedLiveData = MutableLiveData(false)
    var subscriptionReceiptData: SubscriptionReceiptData? = null

    fun startSignUp(
        serverDomain: String,
        isSubscription: Boolean = false
    ) {
        launchBg {
            startSignUpEventLiveData.postValue(
                dataSource.startNewRegistration(
                    serverDomain,
                    isSubscription,
                    subscriptionReceiptData
                )
            )
        }
    }

    fun clearSubtitle() {
        dataSource.clearSubtitle()
    }

    fun getLastActiveSubscriptionReceipt(subscriptionManager: SubscriptionManager) {
        launchBg {
            when (val result = subscriptionManager.getActiveSubscriptionReceipt()) {
                is Response.Success -> {
                    subscriptionReceiptData = result.data
                    isSubscribedLiveData.postValue(true)
                }

                is Response.Error -> isSubscribedLiveData.postValue(false)
            }
        }
    }
}