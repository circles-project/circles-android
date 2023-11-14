package org.futo.circles.feature.groups

import org.futo.circles.core.feature.room.RoomListHelper
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.mapping.toInviteGroupListItem
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class GroupsDataSource @Inject constructor(
    private val roomListHelper: RoomListHelper
) {
    fun getGroupsFlow() = roomListHelper.getRoomsFlow(::filterGroups)

    private fun filterGroups(
        list: List<RoomSummary>,
        knownUsersIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): List<GroupListItem> {
        val groups = list.filter { it.roomType == GROUP_TYPE }
        val joinedGroups = groups.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invites = groups.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }
        return mutableListOf<GroupListItem>().apply {
            addAll(invites.map {
                it.toInviteGroupListItem(
                    roomListHelper.shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
                )
            })
            addAll(joinedGroups.map { it.toJoinedGroupListItem() })
        }
    }

    fun unblurProfileImageFor(id: String) = roomListHelper.unblurProfileImageFor(id)
}