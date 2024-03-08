package org.futo.circles.core.feature.user.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemUsersTimelineBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.TimelineHeaderItem
import org.futo.circles.core.model.TimelineListItem
import org.futo.circles.core.model.TimelineRoomListItem


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
            ivTimelineImage.loadRoomProfileIcon(data.info.avatarUrl, data.info.title)
            vLoading.setIsVisible(data.isLoading)
            btnFollow.setIsVisible(!data.isJoined && !data.isLoading)
            btnUnFollow.setIsVisible(data.isJoined && !data.isLoading)
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