package org.futo.circles.feature.room.select.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setSelectableItemBackground
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemSelectRoomBinding
import org.futo.circles.model.SelectableRoomListItem

class SelectRoomsViewHolder(
    parent: ViewGroup,
    private val onCircleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSelectRoomBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSelectRoomBinding

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