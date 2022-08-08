package org.futo.circles.feature.sign_up.subscription_stage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.SubscriptionListItem
import org.futo.circles.subscriptions.SubscriptionManager
import org.matrix.android.sdk.api.auth.registration.RegistrationResult

class SubscriptionStageViewModel(
    private val dataSource: SubscriptionStageDataSource
) : ViewModel() {

    val subscribeLiveData = SingleEventLiveData<Response<RegistrationResult>>()
    val purchaseLiveData = SingleEventLiveData<Response<Unit>>()
    val subscriptionsListLiveData = MutableLiveData<Response<List<SubscriptionListItem>>>()

    fun validateSubscriptionReceipt(receipt: String) {
        launchBg {
            subscribeLiveData.postValue(dataSource.validateSubscriptionReceipt(receipt))
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