package com.futo.circles.ui.groups.list

import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.base.context
import com.futo.circles.databinding.GroupListItemBinding
import com.futo.circles.extensions.loadImage
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.model.GroupListItem
import com.futo.circles.model.GroupListItemPayload

class GroupViewHolder(
    parent: ViewGroup,
    onGroupClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, GroupListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GroupListItemBinding

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    fun bind(data: GroupListItem) {
        with(binding) {
            ivGroup.loadImage(data.avatarUrl, ivGroup.height)

            ivLock.setIsEncryptedIcon(data.isEncrypted)

            tvGroupTitle.text = data.title

            setMembersCount(data.membersCount)

            tvTopic.text = context.getString(
                R.string.topic_formatter,
                data.topic.takeIf { it.isNotEmpty() } ?: context.getString(R.string.none)
            )

            setUpdateTime(data.timestamp)
        }
    }

    fun bindPayload(data: GroupListItemPayload) {
        setMembersCount(data.membersCount)
        setUpdateTime(data.timestamp)
    }

    private fun setMembersCount(membersCount: Int) {
        binding.tvMembers.text = context.resources.getQuantityString(
            R.plurals.member_plurals,
            membersCount, membersCount
        )
    }

    private fun setUpdateTime(timestamp: Long) {
        binding.tvUpdateTime.text = context.getString(
            R.string.last_updated_formatter, DateUtils.getRelativeTimeSpanString(
                timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }
}