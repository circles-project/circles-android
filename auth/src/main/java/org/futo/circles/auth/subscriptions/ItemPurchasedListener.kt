package org.futo.circles.auth.subscriptions

import org.futo.circles.auth.model.SubscriptionReceiptData


interface ItemPurchasedListener {

    fun onItemPurchased(subscriptionReceiptData: SubscriptionReceiptData)

    fun onPurchaseFailed(errorCode: Int)

}