package org.futo.circles.auth.subscriptions

import androidx.fragment.app.Fragment

interface SubscriptionProvider {

    fun getManager(
        fragment: Fragment,
        itemPurchaseListener: ItemPurchasedListener?
    ): SubscriptionManager

}