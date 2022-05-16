package com.futo.circles.feature.circles.accept_invite.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.AcceptCircleInviteListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setSelectableItemBackground
import com.futo.circles.model.SelectableRoomListItem

class CirclesInviteViewHolder(
    parent: ViewGroup,
    private val onCircleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, AcceptCircleInviteListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as AcceptCircleInviteListItemBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    fun bind(data: SelectableRoomListItem) {
        with(binding) {
            setIcon(data)
            tvCircleName.text = data.info.title
        }
    }

    private fun setIcon(data: SelectableRoomListItem) {
        if (data.isSelected) {
            binding.ivCircleImage.setImageResource(R.drawable.ic_check_circle)
            binding.lRoot.setBackgroundColor(context.getColor(R.color.highlight_color))
        } else {
            binding.ivCircleImage.loadProfileIcon(data.info.avatarUrl, data.info.title)
            binding.lRoot.setSelectableItemBackground()
        }
    }
}