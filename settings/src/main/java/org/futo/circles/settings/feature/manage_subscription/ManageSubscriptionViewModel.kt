package org.futo.circles.settings.feature.manage_subscription

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.model.ActiveSubscriptionInfo
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ManageSubscriptionViewModel @Inject constructor(
) : ViewModel() {

    val subscriptionInfoLiveData = SingleEventLiveData<Response<ActiveSubscriptionInfo>>()

    fun getSubscriptionInfo(subscriptionManager: SubscriptionManager) {
        launchBg {
            val activeReceipt =
                (subscriptionManager.getActiveSubscriptionReceipt() as? Response.Success)?.data
                    ?: kotlin.run {
                        subscriptionInfoLiveData.postValue(
                            Response.Error("Failed to fetch active subscription info")
                        )
                        return@launchBg
                    }
            val details =
                (subscriptionManager.getDetails(listOf(activeReceipt.productId)) as? Response.Success)?.data?.lastOrNull()
                    ?: kotlin.run {
                        subscriptionInfoLiveData.postValue(
                            Response.Error("Failed to fetch active subscription details")
                        )
                        return@launchBg
                    }
            subscriptionInfoLiveData.postValue(
                Response.Success(
                    ActiveSubscriptionInfo(
                        packageName = activeReceipt.packageName,
                        productId = activeReceipt.productId,
                        purchaseTime = activeReceipt.purchaseTime,
                        isAutoRenewing = activeReceipt.isAutoRenewing,
                        name = details.name,
                        description = details.description,
                        price = details.price,
                        duration = details.duration
                    )
                )
            )
        }
    }


}