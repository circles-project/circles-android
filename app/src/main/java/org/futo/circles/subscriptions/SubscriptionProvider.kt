package org.futo.circles.subscriptions

import android.app.Activity

interface SubscriptionProvider {

    fun getManager(
        activity: Activity,
        itemPurchaseListener: ItemPurchasedListener
    ): SubscriptionManager

}