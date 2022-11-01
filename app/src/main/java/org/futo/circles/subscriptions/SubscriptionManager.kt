package org.futo.circles.subscriptions

import org.futo.circles.extensions.Response
import org.futo.circles.model.SubscriptionListItem
import org.futo.circles.model.SubscriptionReceiptData

interface SubscriptionManager {

    suspend fun getActiveSubscriptionReceipt(): Response<SubscriptionReceiptData>

    suspend fun getDetails(productIds: List<String>): Response<List<SubscriptionListItem>>

    suspend fun purchaseProduct(productId: String): Response<Unit>

}