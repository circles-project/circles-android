package org.futo.circles.feature.direct.tab.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.model.DMListItem
import org.futo.circles.model.DMListItemPayload
import org.futo.circles.model.DMsInvitesNotificationListItem
import org.futo.circles.model.JoinedDMsListItem

enum class DMListItemViewType { Joined, InviteNotification }

class DMsListAdapter(
    private val onDmItemClicked: (DMListItem) -> Unit,
    private val onOpenInvitesClicked: () -> Unit
) : BaseRvAdapter<DMListItem, DMsViewHolder>(
    PayloadIdEntityCallback { old, new ->
        if (new is JoinedDMsListItem && old is JoinedDMsListItem) {
            DMListItemPayload(
                timestamp = new.timestamp.takeIf { it != old.timestamp },
                unreadCount = new.unreadCount.takeIf { it != old.unreadCount },
                userName = new.user.name.takeIf { it != old.user.name },
                avatarUrl = new.user.avatarUrl.takeIf { it != old.user.avatarUrl },
                userId = new.user.id
            )
        } else null
    }
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedDMsListItem -> DMListItemViewType.Joined.ordinal
        is DMsInvitesNotificationListItem -> DMListItemViewType.InviteNotification.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = when (DMListItemViewType.entries[viewType]) {
        DMListItemViewType.Joined -> JoinedDMViewHolder(parent = parent,
            onDMClicked = { position -> onDmItemClicked(getItem(position)) })

        DMListItemViewType.InviteNotification -> DMInviteNotificationViewHolder(
            parent = parent, onClicked = { onOpenInvitesClicked() }
        )
    }

    override fun onBindViewHolder(holder: DMsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: DMsViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                if (payload is DMListItemPayload && holder is JoinedDMViewHolder) {
                    holder.bindPayload(payload)
                }
            }
        }
    }
}