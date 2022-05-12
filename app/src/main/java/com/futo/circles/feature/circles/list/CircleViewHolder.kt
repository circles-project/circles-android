package com.futo.circles.feature.circles.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.InvitedCircleListItemBinding
import com.futo.circles.databinding.JoinedCircleListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.CircleListItemPayload
import com.futo.circles.model.InvitedCircleListItem
import com.futo.circles.model.JoinedCircleListItem

abstract class CircleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: CircleListItem)

    protected fun setIcon(circleIcon: ImageView, avatarUrl: String?, title: String) {
        circleIcon.loadProfileIcon(avatarUrl, title)
    }

    protected fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }
}

class JoinedCircleViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : CircleViewHolder(inflate(parent, JoinedCircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as JoinedCircleListItemBinding

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
        }
    }

    fun bindPayload(data: CircleListItemPayload) {
        setFollowingCount(data.followersCount)
        setFollowedByCount(data.followedByCount)
    }

    private fun setFollowingCount(followersCount: Int) {
        binding.tvFollowing.text = context.getString(R.string.following_format, followersCount)
    }

    private fun setFollowedByCount(followedByCount: Int) {
        binding.tvFollowedBy.text = context.getString(R.string.followed_by_format, followedByCount)
    }
}

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : CircleViewHolder(inflate(parent, InvitedCircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InvitedCircleListItemBinding

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