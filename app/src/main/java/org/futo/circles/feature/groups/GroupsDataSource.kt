package org.futo.circles.feature.groups

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getKnownUsersFlow
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

    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getGroupsFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams()).asFlow(),
        MatrixSessionProvider.getSessionOrThrow().getKnownUsersFlow(),
        roomIdsToUnblurProfile,
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, knownUsers, roomIdsToUnblur, _ ->
        withContext(Dispatchers.IO) {
            filterGroups(roomSummaries, knownUsers.map { it.userId }.toSet(), roomIdsToUnblur)
        }
    }.distinctUntilChanged()


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
                    shouldBlurIconFor(it.roomId, it.inviterId, knownUsersIds, roomIdsToUnblur)
                )
            })
            addAll(joinedGroups.map { it.toJoinedGroupListItem() })
        }
    }

    private fun shouldBlurIconFor(
        roomId: String,
        inviterId: String?,
        knownUserIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): Boolean {
        val isKnownUser = knownUserIds.contains(inviterId)
        val isRoomUnbluredByUser = roomIdsToUnblur.contains(roomId)
        return !isKnownUser && !isRoomUnbluredByUser
    }

    fun unblurProfileImageFor(id: String) {
        roomIdsToUnblurProfile.update { set -> set.toMutableSet().apply { add(id) } }
    }
}