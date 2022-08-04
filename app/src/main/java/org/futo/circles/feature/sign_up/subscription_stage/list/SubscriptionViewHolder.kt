package org.futo.circles.feature.sign_up.subscription_stage.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.SubscriptionListItemBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.subscriptions.SubscriptionData
import org.futo.circles.subscriptions.formatIsoPeriod


class SubscriptionViewHolder(
    parent: ViewGroup,
    private val onSubscriptionClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, SubscriptionListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SubscriptionListItemBinding

    init {
        onClick(itemView) { position -> onSubscriptionClicked(position) }
    }

    fun bind(data: SubscriptionData) {
        with(binding) {
            tvName.text = data.details.name
            tvDetails.text = data.details.description
            val productOffer =
                data.details.subscriptionOfferDetails?.last()?.pricingPhases?.pricingPhaseList?.last()
            tvPrice.text = productOffer?.formattedPrice ?: ""
            tvDuration.text = productOffer?.billingPeriod?.formatIsoPeriod(context) ?: ""
        }
    }
}