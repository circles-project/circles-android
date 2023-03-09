package org.futo.circles.feature.people.user.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemUsersTimelineBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.TimelineHeaderItem
import org.futo.circles.model.TimelineListItem
import org.futo.circles.model.TimelineRoomListItem


abstract class UserTimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: TimelineListItem)
}

class UsersTimelineRoomViewHolder(
    parent: ViewGroup,
    private val onRequestFollow: (Int) -> Unit,
    private val onUnFollow: (Int) -> Unit
) : UserTimelineViewHolder(inflate(parent, ListItemUsersTimelineBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemUsersTimelineBinding

    init {
        onClick(binding.btnFollow) { position -> onRequestFollow(position) }
        onClick(binding.btnUnFollow) { position -> onUnFollow(position) }
    }

    override fun bind(data: TimelineListItem) {
        if (data !is TimelineRoomListItem) return
        with(binding) {
            tvTimelineName.text = data.info.title
            ivTimelineImage.loadProfileIcon(data.info.avatarUrl, data.info.title)
            btnFollow.setIsVisible(!data.isJoined)
            btnUnFollow.setIsVisible(data.isJoined)
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