package org.futo.circles.feature.circles.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemInviteNotificationBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.utils.TextFormatUtils
import org.futo.circles.databinding.ListItemJoinedCircleBinding
import org.futo.circles.model.CircleInvitesNotificationListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CircleListItemPayload
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.JoinedCircleListItem

abstract class CirclesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: CircleListItem)

}


class JoinedCircleViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : CirclesViewHolder(inflate(parent, ListItemJoinedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedCircleBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    override fun bind(data: CircleListItem) {
        if (data !is JoinedCircleListItem) return

        with(binding) {
            ivCircle.loadRoomProfileIcon(data.info.avatarUrl, data.info.title)
            setTitle(tvCircleTitle, data.info.title)
            setFollowingCount(data.followingCount)
            setFollowedByCount(data.followedByCount)
            setRequestsCount(data.knockRequestsCount)
            setUnreadCount(data.unreadCount)
        }
    }

    fun bindPayload(data: CircleListItemPayload) {
        data.followersCount?.let { setFollowingCount(it) }
        data.followedByCount?.let { setFollowedByCount(it) }
        data.unreadCount?.let { setUnreadCount(it) }
        data.knocksCount?.let { setRequestsCount(it) }
    }

    private fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }

    private fun setFollowingCount(followersCount: Int) {
        binding.tvFollowing.text =
            context.getString(org.futo.circles.core.R.string.following_format, followersCount)
    }

    private fun setFollowedByCount(followedByCount: Int) {
        binding.tvFollowedBy.text = context.getString(R.string.followed_by_format, followedByCount)
    }

    private fun setRequestsCount(knockRequestsCount: Int) {
        binding.tvKnockRequests.apply {
            setIsVisible(knockRequestsCount > 0)
            text =
                context.getString(R.string.requests_format, knockRequestsCount)
        }
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }
}

class CircleInviteNotificationViewHolder(
    parent: ViewGroup,
    onClicked: () -> Unit
) : CirclesViewHolder(inflate(parent, ListItemInviteNotificationBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteNotificationBinding

    init {
        onClick(binding.lInviteNotification) { _ -> onClicked() }
    }

    override fun bind(data: CircleListItem) {
        if (data !is CircleInvitesNotificationListItem) return
        binding.tvInvitesMessage.text = TextFormatUtils.getFormattedInvitesKnocksMessage(
            context,
            data.invitesCount,
            data.knocksCount
        )
    }
}


class CircleHeaderViewHolder(
    parent: ViewGroup,
) : CirclesViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: CircleListItem) {
        if (data !is CirclesHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}

