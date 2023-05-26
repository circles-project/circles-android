package org.futo.circles.auth.subscriptions

import androidx.fragment.app.Fragment
import org.futo.circles.auth.subscriptions.google.GoogleSubscriptionsManager

object SubscriptionManagerProvider : SubscriptionProvider {

    override fun getManager(
        fragment: Fragment,
        itemPurchaseListener: ItemPurchasedListener?
    ): SubscriptionManager = GoogleSubscriptionsManager(fragment, itemPurchaseListener)
}