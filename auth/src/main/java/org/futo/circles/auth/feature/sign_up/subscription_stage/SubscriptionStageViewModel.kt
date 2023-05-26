package org.futo.circles.auth.feature.sign_up.subscription_stage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.auth.model.SubscriptionListItem
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SubscriptionStageViewModel(
    private val dataSource: SubscriptionStageDataSource
) : ViewModel() {

    val subscribeLiveData = SingleEventLiveData<Response<RegistrationResult>>()
    val purchaseLiveData = SingleEventLiveData<Response<Unit>>()
    val subscriptionsListLiveData = MutableLiveData<Response<List<SubscriptionListItem>>>()

    fun validateSubscription(subscriptionReceiptData: SubscriptionReceiptData) {
        launchBg {
            subscribeLiveData.postValue(dataSource.validateSubscription(subscriptionReceiptData))
        }
    }

    fun loadSubscriptionsList(subscriptionManager: SubscriptionManager) {
        launchBg {
            subscriptionsListLiveData.postValue(subscriptionManager.getDetails(dataSource.getProductIdsList()))
        }
    }

    fun purchaseProduct(subscriptionManager: SubscriptionManager, productId: String) {
        launchUi {
            purchaseLiveData.postValue(subscriptionManager.purchaseProduct(productId))
        }
    }

}