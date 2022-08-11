package org.futo.circles.subscriptions

import androidx.fragment.app.Fragment

interface SubscriptionProvider {

    fun getManager(
        fragment: Fragment,
        itemPurchaseListener: ItemPurchasedListener
    ): SubscriptionManager

}