package org.futo.circles.feature.groups.list

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ListItemInvitedGroupBinding
import org.futo.circles.databinding.ListItemJoinedGroupBinding
import org.futo.circles.extensions.setIsEncryptedIcon
import org.futo.circles.model.GroupListItem
import org.futo.circles.model.GroupListItemPayload
import org.futo.circles.model.InvitedGroupListItem
import org.futo.circles.model.JoinedGroupListItem


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
) : GroupViewHolder(inflate(parent, ListItemJoinedGroupBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedGroupBinding

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: GroupListItem) {
        if (data !is JoinedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        setTopic(data.topic)
        setMembersCount(data.membersCount, data.knockRequestsCount)
        setUpdateTime(data.timestamp)
        setUnreadCount(data.unreadCount)
    }

    fun bindPayload(data: GroupListItemPayload) {
        data.isEncrypted?.let { setIsEncrypted(binding.ivLock, it) }
        data.topic?.let { setTopic(it) }
        data.membersCount?.let { setMembersCount(it, data.knocksCount ?: 0) }
        data.knocksCount?.let { setMembersCount(data.membersCount?:0, it) }
        data.timestamp?.let { setUpdateTime(it) }
        data.unreadCount?.let { setUnreadCount(it) }
    }

    private fun setTopic(topic: String) {
        binding.tvTopic.setIsVisible(topic.isNotEmpty())
        binding.tvTopic.text = context.getString(R.string.topic_formatter, topic)
    }

    private fun setMembersCount(membersCount: Int, knocksCount: Int) {
        val members = context.resources.getQuantityString(
            R.plurals.member_plurals,
            membersCount, membersCount
        )
        val knocks = if (knocksCount > 0) " / ${
            context.resources.getQuantityString(
                R.plurals.request_plurals,
                knocksCount, knocksCount
            )
        }" else ""

        val membersInfo = members + knocks
        binding.tvMembers.text = membersInfo
    }

    private fun setUpdateTime(timestamp: Long) {
        binding.tvUpdateTime.text = context.getString(
            org.futo.circles.core.R.string.last_updated_formatter,
            DateUtils.getRelativeTimeSpanString(
                timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }
}

class InvitedGroupViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : GroupViewHolder(inflate(parent, ListItemInvitedGroupBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGroupBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: GroupListItem) {
        if (data !is InvitedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        binding.tvInviterName.text =
            context.getString(org.futo.circles.core.R.string.invited_by_format, data.inviterName)
    }
}