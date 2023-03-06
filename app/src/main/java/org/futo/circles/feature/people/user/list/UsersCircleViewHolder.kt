package org.futo.circles.feature.people.user.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemSelectRoomBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.model.TimelineHeaderItem
import org.futo.circles.model.TimelineListItem
import org.futo.circles.model.TimelineRoomListItem


abstract class UserTimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: TimelineListItem)
}

class UsersTimelineRoomViewHolder(
    parent: ViewGroup
) : UserTimelineViewHolder(inflate(parent, ListItemSelectRoomBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSelectRoomBinding

    override fun bind(data: TimelineListItem) {
        if (data !is TimelineRoomListItem) return
        with(binding) {
            tvCircleName.text = data.info.title
            binding.ivCircleImage.loadProfileIcon(data.info.avatarUrl, data.info.title)
        }
    }
}

class UserTimelineHeaderViewHolder(
    parent: ViewGroup,
) : UserTimelineViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: TimelineListItem) {
        if (data !is TimelineHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}