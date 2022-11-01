package org.futo.circles.feature.sign_up.sign_up_type

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.SubscriptionReceiptData
import org.futo.circles.subscriptions.SubscriptionManager

class SelectSignUpTypeViewModel(
    private val dataSource: SelectSignUpTypeDataSource
) : ViewModel() {

    val startSignUpEventLiveData = SingleEventLiveData<Response<Unit?>>()
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