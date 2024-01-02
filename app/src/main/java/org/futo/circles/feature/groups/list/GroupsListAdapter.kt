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
            topic = new.topic.takeIf { it != old.topic },
            isEncrypted = new.isEncrypted.takeIf { it != old.isEncrypted },
            membersCount = new.membersCount.takeIf { it != old.membersCount || new.knockRequestsCount != old.knockRequestsCount },
            knocksCount = new.knockRequestsCount.takeIf { it != old.knockRequestsCount || new.membersCount != old.membersCount },
            timestamp = new.timestamp.takeIf { it != old.timestamp },
            unreadCount = new.unreadCount.takeIf { it != old.unreadCount },
            needUpdateFullItem = new.info.title != old.info.title || new.info.avatarUrl != old.info.avatarUrl
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
                    if (payload.needUpdateFullItem) holder.bind(getItem(position))
                    else holder.bindPayload(payload)
                }
            }
        }
    }
}