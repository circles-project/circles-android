package org.futo.circles.feature.room.manage_members


import android.content.Context
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.R
import org.futo.circles.core.ExpandableItemsDataSource
import org.futo.circles.extensions.createResult
import org.futo.circles.mapping.nameOrId
import org.futo.circles.mapping.toGroupMemberListItem
import org.futo.circles.mapping.toInvitedUserListItem
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper

class ManageMembersDataSource(
    private val roomId: String,
    private val type: CircleRoomTypeArg,
    private val context: Context
) : ExpandableItemsDataSource {

    private val session = MatrixSessionProvider.currentSession
    private val room = session?.getRoom(roomId)
    private var powerLevelsContent: PowerLevelsContent? = null

    override val itemsWithVisibleOptionsFlow: MutableStateFlow<MutableSet<String>> =
        MutableStateFlow(mutableSetOf())

    fun getManageMembersTittle() = context.getString(
        if (type == CircleRoomTypeArg.Group) R.string.group_members_format
        else R.string.followers_for_format,
        room?.roomSummary()?.nameOrId() ?: roomId
    )


    fun getRoomMembersFlow(): Flow<List<ManageMembersListItem>> {
        return combine(
            getRoomMembersSummaryFlow(), getRoomMembersRoleFlow(), itemsWithVisibleOptionsFlow
        ) { members, powerLevel, usersWithVisibleOptions ->
            buildList(members, powerLevel, usersWithVisibleOptions)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }

    private fun getRoomMembersSummaryFlow(): Flow<List<RoomMemberSummary>> {
        val roomMemberQueryParams = roomMemberQueryParams {
            displayName = QueryStringValue.IsNotEmpty
            memberships = Membership.activeMemberships()
        }
        return room?.membershipService()?.getRoomMembersLive(roomMemberQueryParams)?.asFlow()
            ?: flowOf()
    }

    private fun getRoomMembersRoleFlow(): Flow<PowerLevelsContent> {
        return room?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
    }

    private fun buildList(
        members: List<RoomMemberSummary>,
        powerLevelsContent: PowerLevelsContent,
        usersWithVisibleOptions: Set<String>
    ): List<ManageMembersListItem> {
        this.powerLevelsContent = powerLevelsContent
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

    suspend fun removeUser(userId: String) =
        createResult { room?.membershipService()?.remove(userId) }

    suspend fun banUser(userId: String) = createResult { room?.membershipService()?.ban(userId) }

    suspend fun changeAccessLevel(userId: String, levelValue: Int) = createResult {
        val content = powerLevelsContent?.setUserPowerLevel(userId, levelValue).toContent()
        room?.stateService()
            ?.sendStateEvent(EventType.STATE_ROOM_POWER_LEVELS, stateKey = "", content)
    }
}