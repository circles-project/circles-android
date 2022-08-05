package org.futo.circles.feature.sign_up.subscription_stage.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.SubscriptionListItemBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.SubscriptionListItem


class SubscriptionViewHolder(
    parent: ViewGroup,
    private val onSubscriptionClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, SubscriptionListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SubscriptionListItemBinding

    init {
        onClick(itemView) { position -> onSubscriptionClicked(position) }
    }

    fun bind(data: SubscriptionListItem) {
        with(binding) {
            tvName.text = data.name
            tvDetails.text = data.description
            tvPrice.text = data.price
            tvDuration.text = data.duration
        }
    }
}