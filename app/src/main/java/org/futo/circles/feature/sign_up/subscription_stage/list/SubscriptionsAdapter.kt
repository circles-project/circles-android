package org.futo.circles.feature.sign_up.subscription_stage.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.subscriptions.SubscriptionData

class SubscriptionsAdapter(
    private val onItemClicked: (SubscriptionData) -> Unit
) : BaseRvAdapter<SubscriptionData, SubscriptionViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder =
        SubscriptionViewHolder(
            parent = parent,
            onSubscriptionClicked = { position -> onItemClicked(getItem(position)) }
        )

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}