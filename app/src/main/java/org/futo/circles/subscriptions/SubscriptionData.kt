package org.futo.circles.subscriptions

import org.futo.circles.core.list.IdEntity

data class SubscriptionData(
    val sku: String,
    val price: String,
    val duration: String?
) : IdEntity<String> {
    override val id: String get() = sku
}