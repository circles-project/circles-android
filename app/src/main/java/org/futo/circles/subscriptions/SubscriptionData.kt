package org.futo.circles.subscriptions

import com.android.billingclient.api.ProductDetails
import org.futo.circles.core.list.IdEntity

data class SubscriptionData(
    val details: ProductDetails
) : IdEntity<String> {
    override val id: String = details.productId
}