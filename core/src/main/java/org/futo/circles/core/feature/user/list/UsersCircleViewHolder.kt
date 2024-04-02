package org.futo.circles.core.feature.user.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemPeopleDefaultBinding
import org.futo.circles.core.databinding.ListItemUsersTimelineBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.MutualFriendListItem
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

class MutualFriendsViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : UserTimelineViewHolder(inflate(parent, ListItemPeopleDefaultBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleDefaultBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: TimelineListItem) {
        val userItem = (data as? MutualFriendListItem) ?: return
        if (userItem.isIgnored) setUnBlurClick(userItem)
        with(binding) {
            tvUserName.text = userItem.user.name
            tvUserId.text = userItem.user.id
            ivUserImage.loadUserProfileIcon(
                userItem.user.avatarUrl,
                userItem.user.id,
                applyBlur = userItem.isIgnored
            )
            tvIgnoredLabel.setIsVisible(userItem.isIgnored)
            tvShowProfileImage.setIsVisible(userItem.isIgnored)
        }
    }

    private fun setUnBlurClick(userItem: MutualFriendListItem) {
        with(binding) {
            ivUserImage.setOnClickListener {
                ivUserImage.loadUserProfileIcon(
                    userItem.user.avatarUrl,
                    userItem.user.id,
                    applyBlur = false
                )
                tvShowProfileImage.gone()
            }
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