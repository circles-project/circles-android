package org.futo.circles.feature.groups.list

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteNotificationBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.utils.TextFormatUtils
import org.futo.circles.databinding.ListItemJoinedGroupBinding
import org.futo.circles.model.GroupInvitesNotificationListItem
import org.futo.circles.model.GroupListItem
import org.futo.circles.model.GroupListItemPayload
import org.futo.circles.model.JoinedGroupListItem


abstract class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GroupListItem)
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

        binding.ivGroup.loadRoomProfileIcon(data.info.avatarUrl, data.info.title)
        setTitle(binding.tvGroupTitle, data.info.title)
        setMembersCount(data.membersCount)
        setUnreadCount(data.unreadCount)
    }

    fun bindPayload(data: GroupListItemPayload) {
        data.membersCount?.let { setMembersCount(it) }
        data.unreadCount?.let { setUnreadCount(it) }
    }

    private fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }

    private fun setMembersCount(membersCount: Int) {
        binding.tvMembers.text = context.resources.getQuantityString(
            R.plurals.member_plurals,
            membersCount, membersCount
        )
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }
}

class GroupInviteNotificationViewHolder(
    parent: ViewGroup,
    onClicked: () -> Unit
) : GroupViewHolder(inflate(parent, ListItemInviteNotificationBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteNotificationBinding

    init {
        onClick(binding.lInviteNotification) { _ -> onClicked() }
    }

    override fun bind(data: GroupListItem) {
        if (data !is GroupInvitesNotificationListItem) return
        binding.tvInvitesMessage.text = TextFormatUtils.getFormattedInvitesKnocksMessage(
            context,
            data.invitesCount,
            data.knockRequestsCount
        )
    }
}