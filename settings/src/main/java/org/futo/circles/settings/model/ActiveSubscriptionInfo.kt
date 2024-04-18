package org.futo.circles.settings.model

data class ActiveSubscriptionInfo(
    val packageName: String,
    val productId: String,
    val purchaseTime: Long,
    val isAutoRenewing: Boolean,
    val name: String,
    val description: String,
    val price: String,
    val duration: String
)