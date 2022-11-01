package org.futo.circles.subscriptions

import org.futo.circles.model.SubscriptionReceiptData


interface ItemPurchasedListener {

    fun onItemPurchased(subscriptionReceiptData: SubscriptionReceiptData)

    fun onPurchaseFailed(errorCode: Int)

}