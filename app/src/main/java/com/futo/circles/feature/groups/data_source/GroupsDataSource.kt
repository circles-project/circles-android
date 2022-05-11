package com.futo.circles.feature.groups.data_source

import com.futo.circles.core.matrix.room.RoomRelationsBuilder
import com.futo.circles.extensions.createResult
import com.futo.circles.mapping.toInviteGroupListItem
import com.futo.circles.mapping.toJoinedGroupListItem
import com.futo.circles.model.GROUP_TYPE
import com.futo.circles.model.GroupListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupsDataSource(
    private val roomRelationsBuilder: RoomRelationsBuilder
) {

    val session = MatrixSessionProvider.currentSession

    fun filterGroups(list: List<RoomSummary>): List<GroupListItem> {
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
        session?.joinRoom(roomId)
        roomRelationsBuilder.setInvitedGroupRelations(roomId)
    }

    suspend fun rejectInvite(roomId: String) = createResult {
        session?.leaveRoom(roomId)
    }
}