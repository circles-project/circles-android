package org.futo.circles.feature.groups

import androidx.lifecycle.map
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.mapping.toInviteGroupListItem
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GroupListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class GroupsDataSource @Inject constructor(
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    fun getGroupsLiveData() = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummariesLive(roomSummaryQueryParams())
        ?.map { list -> filterGroups(list) }

    suspend fun rejectInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.leaveRoom(roomId)
    }

    private fun filterGroups(list: List<RoomSummary>): List<GroupListItem> {
        return list.mapNotNull { summary ->
            if (summary.roomType == GROUP_TYPE) {
                when (summary.membership) {
                    Membership.INVITE -> summary.toInviteGroupListItem()
                    Membership.JOIN -> summary.toJoinedGroupListItem()
                    else -> null
                }
            } else null
        }.sortedBy { it.membership }
    }

    suspend fun acceptInvite(roomId: String) = createResult {
        MatrixSessionProvider.currentSession?.roomService()?.joinRoom(roomId)
        roomRelationsBuilder.setInvitedGroupRelations(roomId)
    }
}