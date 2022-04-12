package com.futo.circles.feature.circles.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.CircleListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.model.CircleListItem
import com.futo.circles.model.CircleListItemPayload

class CircleViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, CircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as CircleListItemBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    fun bind(data: CircleListItem) {
        with(binding) {
            ivCircle.loadProfileIcon(data.avatarUrl, data.name)
            tvCircleTitle.text = data.name
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