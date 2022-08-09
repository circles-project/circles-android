package org.futo.circles.feature.groups

import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.core.rooms.data_source.RoomsDataSource
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.toInviteGroupListItem
import org.futo.circles.mapping.toJoinedGroupListItem
import org.futo.circles.model.GROUP_TYPE
import org.futo.circles.model.RoomListItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupsDataSource(
    private val roomRelationsBuilder: RoomRelationsBuilder
) : RoomsDataSource() {

    override fun filterRooms(list: List<RoomSummary>): List<RoomListItem> {
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
        session?.roomService()?.joinRoom(roomId)
        roomRelationsBuilder.setInvitedGroupRelations(roomId)
    }
}