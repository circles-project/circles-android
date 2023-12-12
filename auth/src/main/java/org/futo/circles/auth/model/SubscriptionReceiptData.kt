package org.futo.circles.auth.model

data class SubscriptionReceiptData(
    val productId: String,
    val purchaseToken: String,
    val orderId: String,
    val packageName: String
)