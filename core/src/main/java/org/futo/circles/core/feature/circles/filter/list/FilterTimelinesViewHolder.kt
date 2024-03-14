package org.futo.circles.core.feature.circles.filter.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemTimelineFilterBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.FilterTimelinesListItem

class FilterTimelinesViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemTimelineFilterBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemTimelineFilterBinding

    init {
        onClick(itemView) { position ->
            binding.vCheck.isChecked = !binding.vCheck.isChecked
            onItemClicked(position)
        }
    }

    fun bind(data: FilterTimelinesListItem) {
        with(binding) {
            tvRoomName.text = data.name
            tvOwnerName.text = data.ownerName
            ivRoom.loadRoomProfileIcon(data.avatarUrl, data.name)
            vCheck.isChecked = data.isSelected
        }
    }
}