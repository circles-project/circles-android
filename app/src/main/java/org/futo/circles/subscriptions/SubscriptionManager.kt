package org.futo.circles.subscriptions

interface SubscriptionManager {

    suspend fun getDetails(productIds: List<String>): BillingResult<List<SubscriptionData>>

    suspend fun purchaseProduct(productId: String): BillingResult<String>

}