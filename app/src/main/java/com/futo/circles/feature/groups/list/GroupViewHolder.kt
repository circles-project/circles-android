package com.futo.circles.feature.groups.list

import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.GroupListItemBinding
import com.futo.circles.extensions.loadProfileIcon
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
        setIcon(data.avatarUrl, data.title)
        setIsEncrypted(data.isEncrypted)
        setTitle(data.title)
        setMembersCount(data.membersCount)
        setTopic(data.topic)
        setUpdateTime(data.timestamp)
    }

    fun bindPayload(data: GroupListItemPayload) {
        data.isEncrypted?.let { setIsEncrypted(it) }
        data.topic?.let { setTopic(it) }
        data.membersCount?.let { setMembersCount(it) }
        data.timestamp?.let { setUpdateTime(it) }
    }

    private fun setIcon(avatarUrl: String?, title: String) {
        binding.ivGroup.loadProfileIcon(avatarUrl, title)
    }

    private fun setIsEncrypted(isEncrypted: Boolean) {
        binding.ivLock.setIsEncryptedIcon(isEncrypted)
    }

    private fun setTitle(title: String) {
        binding.tvGroupTitle.text = title
    }

    private fun setTopic(topic: String) {
        binding.tvTopic.text = context.getString(
            R.string.topic_formatter,
            topic.takeIf { it.isNotEmpty() } ?: context.getString(R.string.none)
        )
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