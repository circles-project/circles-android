package com.futo.circles.feature.manage_group_members.data_source


import android.content.Context
import androidx.lifecycle.asFlow
import com.futo.circles.R
import com.futo.circles.mapping.nameOrId
import com.futo.circles.mapping.toGroupMemberListItem
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper

class ManageGroupMembersDataSource(
    private val roomId: String,
    private val context: Context
) {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)


    fun getManageMembersTittle() = context.getString(
        R.string.group_members_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

    fun getRoomMembersFlow(): Flow<List<GroupMemberListItem>> {
        return combine(
            getRoomMembersSummaryFlow(), getRoomMembersRoleFlow()
        ) { members, powerLevel ->
            buildList(members, powerLevel)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    private fun getRoomMembersSummaryFlow(): Flow<List<RoomMemberSummary>> {
        val roomMemberQueryParams = roomMemberQueryParams {
            displayName = QueryStringValue.IsNotEmpty
            memberships = Membership.activeMemberships()
        }
        return room?.getRoomMembersLive(roomMemberQueryParams)?.asFlow() ?: flowOf()
    }

    private fun getRoomMembersRoleFlow(): Flow<PowerLevelsContent> {
        return room?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS)?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
    }

    private fun buildList(
        members: List<RoomMemberSummary>,
        powerLevelsContent: PowerLevelsContent
    ): List<GroupMemberListItem> {
        val roleHelper = PowerLevelsHelper(powerLevelsContent)

        return members.map { member ->
            val role = roleHelper.getUserRole(member.userId)
            val hasInvite = member.membership == Membership.INVITE

            member.toGroupMemberListItem(role, hasInvite)

        }.sortedByDescending { it.role.value }
    }
}