package org.futo.circles.feature.groups

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.mapping.toInviteGroupListItem
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class GroupsDataSource @Inject constructor() {

    fun getGroupsFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams()).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { filterGroups(roomSummaries) }
    }.distinctUntilChanged()


    private fun filterGroups(list: List<RoomSummary>): List<GroupListItem> {
        val groups = list.filter { it.roomType == GROUP_TYPE }
        val joinedGroups = groups.mapNotNull { it.takeIf { it.membership == Membership.JOIN } }
        val invites = groups.mapNotNull { it.takeIf { it.membership == Membership.INVITE } }
        return mutableListOf<GroupListItem>().apply {
            addAll(invites.map { it.toInviteGroupListItem() })
            addAll(joinedGroups.map { it.toJoinedGroupListItem() })
        }
    }
}