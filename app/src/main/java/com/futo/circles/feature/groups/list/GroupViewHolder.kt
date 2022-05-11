package com.futo.circles.feature.groups.list

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.InvitedGroupListItemBinding
import com.futo.circles.databinding.JoinedGroupListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.model.GroupListItem
import com.futo.circles.model.InvitedGroupListItem
import com.futo.circles.model.JoinedGroupListItem

abstract class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GroupListItem)

    protected fun setIcon(groupIcon: ImageView, avatarUrl: String?, title: String) {
        groupIcon.loadProfileIcon(avatarUrl, title)
    }

    protected fun setIsEncrypted(lockIcon: ImageView, isEncrypted: Boolean) {
        lockIcon.setIsEncryptedIcon(isEncrypted)
    }

    protected fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }
}

class JoinedGroupViewHolder(
    parent: ViewGroup,
    onGroupClicked: (Int) -> Unit
) : GroupViewHolder(inflate(parent, JoinedGroupListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as JoinedGroupListItemBinding

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: GroupListItem) {
        if (data !is JoinedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.info.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        binding.tvTopic.text = context.getString(
            R.string.topic_formatter,
            data.topic.takeIf { it.isNotEmpty() } ?: context.getString(R.string.none)
        )
        binding.tvMembers.text = context.resources.getQuantityString(
            R.plurals.member_plurals,
            data.membersCount, data.membersCount
        )
        binding.tvUpdateTime.text = context.getString(
            R.string.last_updated_formatter, DateUtils.getRelativeTimeSpanString(
                data.timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }
}

class InvitedGroupViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : GroupViewHolder(inflate(parent, InvitedGroupListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InvitedGroupListItemBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: GroupListItem) {
        if (data !is InvitedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.info.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        binding.tvInviterName.text = context.getString(R.string.invited_by_format, data.inviterName)
    }

}