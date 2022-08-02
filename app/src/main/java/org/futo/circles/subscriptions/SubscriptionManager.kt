package org.futo.circles.subscriptions

interface SubscriptionManager {

    suspend fun getDetails(): BillingResult<List<SubscriptionData>>

    suspend fun getLastSubscriptionItem(): BillingResult<String>

    suspend fun purchase(sku: String): BillingResult<String>

}