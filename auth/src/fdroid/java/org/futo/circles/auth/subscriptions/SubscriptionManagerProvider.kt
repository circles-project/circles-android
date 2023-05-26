package org.futo.circles.auth.subscriptions

import androidx.fragment.app.Fragment
import org.futo.circles.auth.R

object SubscriptionManagerProvider : SubscriptionProvider {

    override fun getManager(
        fragment: Fragment,
        itemPurchaseListener: ItemPurchasedListener?
    ): SubscriptionManager =
        throw IllegalStateException(fragment.getString(R.string.flavour_does_not_support_subscriptions))
}