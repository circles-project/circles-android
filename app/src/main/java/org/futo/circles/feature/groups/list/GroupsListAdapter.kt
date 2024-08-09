package org.futo.circles.feature.groups.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.model.GroupInvitesNotificationListItem
import org.futo.circles.model.GroupListItem
import org.futo.circles.model.GroupListItemPayload
import org.futo.circles.model.JoinedGroupListItem

enum class GroupListItemViewType { JoinedGroup, InviteNotification }

class GroupsListAdapter(
    private val onRoomClicked: (GroupListItem) -> Unit,
    private val onOpenInvitesClicked: () -> Unit
) : BaseRvAdapter<GroupListItem, GroupViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is JoinedGroupListItem && old is JoinedGroupListItem) {
        GroupListItemPayload(
            title = new.info.title.takeIf { it != old.info.title },
            avatarUrl = new.info.avatarUrl.takeIf { it != old.info.avatarUrl },
            membersCount = new.membersCount.takeIf { it != old.membersCount },
            unreadCount = new.unreadCount.takeIf { it != old.unreadCount }
        )
    } else null
}) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is JoinedGroupListItem -> GroupListItemViewType.JoinedGroup.ordinal
        is GroupInvitesNotificationListItem -> GroupListItemViewType.InviteNotification.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = when (GroupListItemViewType.entries[viewType]) {
        GroupListItemViewType.JoinedGroup -> JoinedGroupViewHolder(parent = parent,
            onGroupClicked = { position -> onRoomClicked(getItem(position)) })

        GroupListItemViewType.InviteNotification -> GroupInviteNotificationViewHolder(
            parent = parent, onClicked = { onOpenInvitesClicked() }
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: GroupViewHolder, position: Int, payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                if (payload is GroupListItemPayload && holder is JoinedGroupViewHolder) {
                    holder.bindPayload(payload)
                }
            }
        }
    }
}