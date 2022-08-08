package org.futo.circles.subscriptions

import android.app.Activity
import org.futo.circles.subscriptions.google.GoogleSubscriptionsManager

object SubscriptionManagerProvider : SubscriptionProvider {

    override fun getManager(
        activity: Activity,
        itemPurchaseListener: ItemPurchasedListener
    ): SubscriptionManager = GoogleSubscriptionsManager(activity, itemPurchaseListener)
}