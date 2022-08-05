package org.futo.circles.subscriptions

import org.futo.circles.extensions.Response
import org.futo.circles.model.SubscriptionListItem

interface SubscriptionManager {

    suspend fun getDetails(productIds: List<String>): Response<List<SubscriptionListItem>>

    suspend fun purchaseProduct(productId: String): Response<Unit>

}