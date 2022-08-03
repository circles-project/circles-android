package org.futo.circles.subscriptions

interface SubscriptionManager {

    suspend fun getDetails(): BillingResult<List<SubscriptionData>>

    suspend fun purchaseProduct(sku: String): BillingResult<String>

}