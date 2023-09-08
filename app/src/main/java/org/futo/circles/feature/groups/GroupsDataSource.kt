package org.futo.circles.feature.groups

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import org.futo.circles.core.mapping.toRoomInfo
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.futo.circles.mapping.toInviteGroupListItem
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GroupListItem
import org.futo.circles.model.RequestGroupListItem
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
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
        val knocks = getKnockRequestToJoinedGroups(joinedGroups)
        return mutableListOf<GroupListItem>().apply {
            addAll(knocks)
            addAll(invites.map { it.toInviteGroupListItem() })
            addAll(joinedGroups.map { it.toJoinedGroupListItem() })
        }
    }

    private fun getKnockRequestToJoinedGroups(joinedGroups: List<RoomSummary>): List<RequestGroupListItem> {
        val requests = mutableListOf<RequestGroupListItem>()

        joinedGroups.forEach { groupSummary ->
            val group =
                MatrixSessionProvider.currentSession?.getRoom(groupSummary.roomId) ?: return@forEach

            val knockingMembers =
                group.membershipService().getRoomMembers(roomMemberQueryParams {
                    memberships = listOf(Membership.KNOCK)
                }).takeIf { it.isNotEmpty() } ?: return@forEach


            knockingMembers.forEach { user ->
                requests.add(
                    RequestGroupListItem(
                        id = groupSummary.roomId,
                        info = groupSummary.toRoomInfo(),
                        requesterName = user.displayName
                            ?: UserUtils.removeDomainSuffix(user.userId),
                        requesterId = user.userId
                    )
                )
            }
        }
        return requests
    }
}