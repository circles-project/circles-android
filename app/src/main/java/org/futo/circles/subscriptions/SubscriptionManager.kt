package org.futo.circles.subscriptions

import com.android.billingclient.api.ProductDetails

interface SubscriptionManager {

    suspend fun getDetails(): BillingResult<List<SubscriptionData>>

    suspend fun purchaseProduct(productDetails: ProductDetails): BillingResult<String>

}