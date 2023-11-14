package org.futo.circles.feature.circles.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ListItemInvitedCircleBinding
import org.futo.circles.databinding.ListItemJoinedCircleBinding
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CircleListItemPayload
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.InvitedCircleListItem
import org.futo.circles.model.JoinedCircleListItem

abstract class CirclesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: CircleListItem)

    protected fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }
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
            ivCircle.loadProfileIcon(data.info.avatarUrl, data.info.title)
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

    private fun setFollowingCount(followersCount: Int) {
        binding.tvFollowing.text = context.getString(R.string.following_format, followersCount)
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

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : CirclesViewHolder(inflate(parent, ListItemInvitedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedCircleBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivCircle) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: CircleListItem) {
        if (data !is InvitedCircleListItem) return

        with(binding) {
            tvShowProfileImage.setIsVisible(data.shouldBlurIcon)
            ivCircle.loadProfileIcon(
                data.info.avatarUrl,
                data.info.title,
                applyBlur = data.shouldBlurIcon
            )
            setTitle(tvCircleTitle, data.info.title)
            binding.tvInvitedBy.text =
                context.getString(
                    org.futo.circles.core.R.string.invited_by_format,
                    data.inviterName
                )
        }
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

