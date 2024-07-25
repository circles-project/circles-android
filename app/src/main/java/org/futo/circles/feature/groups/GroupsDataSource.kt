package org.futo.circles.feature.groups

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getGroupsLiveData
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GroupInvitesNotificationListItem
import org.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class GroupsDataSource @Inject constructor() {

    fun getGroupsFlow() = combine(
        getGroupsLiveData().asFlow(),
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive()
            .asFlow() //for knocks count
    ) { roomSummaries, _ ->
        withContext(Dispatchers.IO) { buildList(roomSummaries) }
    }.distinctUntilChanged()


    private fun buildList(groups: List<RoomSummary>): List<GroupListItem> {
        val joinedGroups = groups
            .filter { it.membership == Membership.JOIN }
            .map { it.toJoinedGroupListItem() }

        val invitesCount = groups.filter { it.membership == Membership.INVITE }.size
        var knocksCount = 0
        joinedGroups.forEach { knocksCount += it.knockRequestsCount }

        return mutableListOf<GroupListItem>().apply {
            if (invitesCount > 0 || knocksCount > 0) {
                add(GroupInvitesNotificationListItem(invitesCount, knocksCount))
            }

            addAll(joinedGroups)
        }
    }
}