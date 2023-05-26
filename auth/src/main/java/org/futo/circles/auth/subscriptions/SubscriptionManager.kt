package org.futo.circles.auth.subscriptions

import org.futo.circles.auth.model.SubscriptionListItem
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.core.extensions.Response

interface SubscriptionManager {

    suspend fun getActiveSubscriptionReceipt(): Response<SubscriptionReceiptData>

    suspend fun getDetails(productIds: List<String>): Response<List<SubscriptionListItem>>

    suspend fun purchaseProduct(productId: String): Response<Unit>

}