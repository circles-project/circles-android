package org.futo.circles.feature.direct.tab.list

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteNotificationBinding
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.utils.TextFormatUtils.getFormattedInvitesKnocksMessage
import org.futo.circles.databinding.ListItemJoinedDmBinding
import org.futo.circles.model.DMListItem
import org.futo.circles.model.DMListItemPayload
import org.futo.circles.model.DMsInvitesNotificationListItem
import org.futo.circles.model.JoinedDMsListItem


abstract class DMsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: DMListItem)
}

class JoinedDMViewHolder(
    parent: ViewGroup,
    onDMClicked: (Int) -> Unit
) : DMsViewHolder(inflate(parent, ListItemJoinedDmBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedDmBinding

    init {
        onClick(itemView) { position -> onDMClicked(position) }
    }

    override fun bind(data: DMListItem) {
        if (data !is JoinedDMsListItem) return
        with(binding) {
            tvUserName.text = data.user.name
            tvUserId.text = data.user.id
            ivUserImage.loadUserProfileIcon(
                data.user.avatarUrl,
                data.user.id
            )
        }
        setUpdateTime(data.timestamp)
        setUnreadCount(data.unreadCount)
    }

    private fun setUpdateTime(timestamp: Long) {
        binding.tvLastUpdated.text = context.getString(
            org.futo.circles.core.R.string.last_updated_formatter,
            DateUtils.getRelativeTimeSpanString(
                timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }

    fun bindPayload(data: DMListItemPayload) {
        data.timestamp?.let { setUpdateTime(it) }
        data.unreadCount?.let { setUnreadCount(it) }
        data.userName?.let { binding.tvUserName.text = it }
        data.avatarUrl?.let {
            binding.ivUserImage.loadUserProfileIcon(it, data.userId)
        }
    }
}

class DMInviteNotificationViewHolder(
    parent: ViewGroup,
    onClicked: () -> Unit
) : DMsViewHolder(inflate(parent, ListItemInviteNotificationBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteNotificationBinding

    init {
        onClick(binding.lInviteNotification) { _ -> onClicked() }
    }

    override fun bind(data: DMListItem) {
        if (data !is DMsInvitesNotificationListItem) return
        binding.tvInvitesMessage.text =
            getFormattedInvitesKnocksMessage(context, data.invitesCount, 0)
    }
}