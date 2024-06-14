package org.futo.circles.core.feature.room.invites.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.model.RoomRequestHeaderItem
import org.futo.circles.core.model.RoomRequestListItem

abstract class RoomRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: RoomRequestListItem)
}

class RoomRequestHeaderViewHolder(
    parent: ViewGroup,
) : RoomRequestViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: RoomRequestListItem) {
        if (data !is RoomRequestHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}
