package org.futo.circles.feature.circles.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemInvitedCircleBinding
import org.futo.circles.databinding.ListItemJoinedCircleBinding
import org.futo.circles.databinding.ListItemRequestCircleBinding
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CircleListItemPayload
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.InvitedCircleListItem
import org.futo.circles.model.JoinedCircleListItem
import org.futo.circles.model.RequestCircleListItem

abstract class CirclesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: CircleListItem)

    protected fun setIcon(groupIcon: ImageView, avatarUrl: String?, title: String) {
        groupIcon.loadProfileIcon(avatarUrl, title)
    }

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
            setIcon(ivCircle, data.info.avatarUrl, data.info.title)
            setTitle(tvCircleTitle, data.info.title)
            setFollowingCount(data.followingCount)
            setFollowedByCount(data.followedByCount)
            setUnreadCount(data.unreadCount)
        }
    }

    fun bindPayload(data: CircleListItemPayload) {
        data.followersCount?.let { setFollowingCount(it) }
        data.followedByCount?.let { setFollowedByCount(it) }
        data.unreadCount?.let { setUnreadCount(it) }
    }

    private fun setFollowingCount(followersCount: Int) {
        binding.tvFollowing.text = context.getString(R.string.following_format, followersCount)
    }

    private fun setFollowedByCount(followedByCount: Int) {
        binding.tvFollowedBy.text = context.getString(R.string.followed_by_format, followedByCount)
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }
}

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : CirclesViewHolder(inflate(parent, ListItemInvitedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedCircleBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: CircleListItem) {
        if (data !is InvitedCircleListItem) return

        with(binding) {
            setIcon(ivCircle, data.info.avatarUrl, data.info.title)
            setTitle(tvCircleTitle, data.info.title)
            binding.tvInvitedBy.text =
                context.getString(R.string.invited_by_format, data.inviterName)
        }
    }
}

class RequestedCircleViewHolder(
    parent: ViewGroup,
    onRequestClicked: (Int, Boolean) -> Unit
) : CirclesViewHolder(inflate(parent, ListItemRequestCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemRequestCircleBinding

    init {
        onClick(binding.btnInvite) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    override fun bind(data: CircleListItem) {
        if (data !is RequestCircleListItem) return

        with(binding) {
            setIcon(ivCircle, data.info.avatarUrl, data.info.title)
            binding.tvRequestUserId.text = context.getString(
                R.string.requested_to_follow_format, data.requesterName
            )
            binding.tvTimelineTitle.text = data.info.title
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

