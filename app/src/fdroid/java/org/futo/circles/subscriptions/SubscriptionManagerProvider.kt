package org.futo.circles.subscriptions

import android.app.Activity
import org.futo.circles.R

object SubscriptionManagerProvider : SubscriptionProvider {

    override fun getManager(
        activity: Activity,
        itemPurchaseListener: ItemPurchasedListener
    ): SubscriptionManager =
        throw IllegalStateException(activity.getString(R.string.flavour_does_not_support_subscriptions))
}