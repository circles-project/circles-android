package org.futo.circles.auth.feature.uia.stages.subscription_stage.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.databinding.ListItemSubscriptionBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.auth.model.SubscriptionListItem
import org.futo.circles.core.base.list.ViewBindingHolder


class SubscriptionViewHolder(
    parent: ViewGroup,
    private val onSubscriptionClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSubscriptionBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSubscriptionBinding

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