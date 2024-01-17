package org.futo.circles.auth.feature.sign_up.subscription_stage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.model.SubscriptionListItem
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

@HiltViewModel
class SubscriptionStageViewModel @Inject constructor(
    private val dataSource: SubscriptionStageDataSource
) : ViewModel() {

    val subscribeLiveData = SingleEventLiveData<Response<RegistrationResult>>()
    val purchaseLiveData = SingleEventLiveData<Response<Unit>>()
    val subscriptionsListLiveData = MutableLiveData<Response<List<SubscriptionListItem>>>()

    fun initiateSubscriptionStage(subscriptionManager: SubscriptionManager) {
        launchBg {
            when (val activeSubscriptionResult =
                getLastActiveSubscriptionReceipt(subscriptionManager)) {
                is Response.Success -> validateSubscription(activeSubscriptionResult.data)
                is Response.Error -> loadSubscriptionsList(subscriptionManager)
            }
        }
    }

    fun validateSubscription(subscriptionReceiptData: SubscriptionReceiptData) {
        launchBg {
            subscribeLiveData.postValue(dataSource.validateSubscription(subscriptionReceiptData))
        }
    }

    fun purchaseProduct(subscriptionManager: SubscriptionManager, productId: String) {
        launchUi {
            purchaseLiveData.postValue(subscriptionManager.purchaseProduct(productId))
        }
    }

    private suspend fun loadSubscriptionsList(subscriptionManager: SubscriptionManager) {
        val details = subscriptionManager.getDetails(dataSource.getProductIdsList())
        subscriptionsListLiveData.postValue(details)
    }

    private suspend fun getLastActiveSubscriptionReceipt(subscriptionManager: SubscriptionManager) =
        subscriptionManager.getActiveSubscriptionReceipt()

}