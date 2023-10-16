package org.futo.circles.core.feature.room.circles.following.list

import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ListItemFollowingBinding
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.FollowingListItem

class FollowingViewHolder(
    parent: ViewGroup,
    onRemoveClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemFollowingBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemFollowingBinding

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