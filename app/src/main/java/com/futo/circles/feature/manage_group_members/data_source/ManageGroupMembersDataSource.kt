package com.futo.circles.feature.manage_group_members.data_source


import android.content.Context
import androidx.lifecycle.asFlow
import com.futo.circles.R
import com.futo.circles.mapping.nameOrId
import com.futo.circles.mapping.toGroupMemberListItem
import com.futo.circles.mapping.toInvitedUserListItem
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.model.InvitedUserListItem
import com.futo.circles.model.ManageMembersHeaderListItem
import com.futo.circles.model.ManageMembersListItem
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

    private val usersWithVisibleOptionsFlow = MutableStateFlow<MutableSet<String>>(mutableSetOf())

    fun getManageMembersTittle() = context.getString(
        R.string.group_members_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )

    fun toggleOptionsVisibilityFor(userId: String) {
        val isOptionsVisible = usersWithVisibleOptionsFlow.value.contains(userId)
        usersWithVisibleOptionsFlow.update { value ->
            val newSet = mutableSetOf<String>().apply { addAll(value) }
            if (isOptionsVisible) newSet.remove(userId)
            else newSet.add(userId)
            newSet
        }
    }

    fun getRoomMembersFlow(): Flow<List<ManageMembersListItem>> {
        return combine(
            getRoomMembersSummaryFlow(), getRoomMembersRoleFlow(), usersWithVisibleOptionsFlow
        ) { members, powerLevel, usersWithVisibleOptions ->
            buildList(members, powerLevel, usersWithVisibleOptions)
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
        powerLevelsContent: PowerLevelsContent,
        usersWithVisibleOptions: Set<String>
    ): List<ManageMembersListItem> {
        val roleHelper = PowerLevelsHelper(powerLevelsContent)
        val fullList = mutableListOf<ManageMembersListItem>()
        val currentMembers = mutableListOf<GroupMemberListItem>()
        val invitedUsers = mutableListOf<InvitedUserListItem>()

        members.forEach { member ->
            if (member.membership == Membership.INVITE) {
                invitedUsers.add(member.toInvitedUserListItem(powerLevelsContent))
            } else {
                val role = roleHelper.getUserRole(member.userId)
                val isOptionsVisible = usersWithVisibleOptions.contains(member.userId)
                currentMembers.add(
                    member.toGroupMemberListItem(
                        role, isOptionsVisible, powerLevelsContent
                    )
                )
            }
        }

        if (currentMembers.isNotEmpty()) {
            fullList.add(ManageMembersHeaderListItem(context.getString(R.string.current_members)))
            fullList.addAll(currentMembers.sortedByDescending { it.role.value })
        }

        if (invitedUsers.isNotEmpty()) {
            fullList.add(ManageMembersHeaderListItem(context.getString(R.string.invited_users)))
            fullList.addAll(invitedUsers)
        }

        return fullList
    }
}