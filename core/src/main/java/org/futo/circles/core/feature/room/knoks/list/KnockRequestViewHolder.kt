package org.futo.circles.core.feature.room.knoks.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemKnockRequestBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.toCircleUser


class KnockRequestViewHolder(
    parent: ViewGroup,
    onRequestClicked: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemKnockRequestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemKnockRequestBinding

    init {
        onClick(binding.btnInvite) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    fun bind(data: KnockRequestListItem) {
        with(binding) {
            setLoading(data.isLoading)
            tvReason.apply {
                setIsVisible(!data.message.isNullOrBlank())
                text = data.message
            }
            vUserLayout.bind(data.toCircleUser())
        }
    }

    private fun setLoading(isLoading: Boolean) {
        with(binding) {
            vLoading.setIsVisible(isLoading)
            btnInvite.setIsVisible(!isLoading)
            btnDecline.setIsVisible(!isLoading)
        }
    }
}