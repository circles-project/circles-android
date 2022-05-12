package com.futo.circles.feature.circles.following.list

import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.FollowingListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.FollowingListItem

class FollowingViewHolder(
    parent: ViewGroup,
    onRemoveClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, FollowingListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as FollowingListItemBinding

    init {
        onClick(binding.ivRemove) { position -> onRemoveClicked(position) }
    }

    fun bind(data: FollowingListItem) {
        binding.tvCircleName.text = data.name
        binding.tvUserName.text = data.ownerName
        binding.ivRoom.loadProfileIcon(data.avatarUrl, data.name)
        binding.tvUpdateTime.text = context.getString(
            R.string.last_updated_formatter, DateUtils.getRelativeTimeSpanString(
                data.updatedTime, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
        binding.ivRemove.setIsVisible(!data.isMyTimeline)
    }
}