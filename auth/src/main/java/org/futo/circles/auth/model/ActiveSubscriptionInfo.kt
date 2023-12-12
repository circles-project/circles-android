package org.futo.circles.auth.model

data class ActiveSubscriptionInfo(
    val productId: String,
    val purchaseTime: Long,
    val isAutoRenewing: Boolean,
    val name: String,
    val description: String,
    val price: String,
    val duration: String
)