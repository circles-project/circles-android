package org.futo.circles.feature.room.manage_members


import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.R
import org.futo.circles.core.ExpandableItemsDataSource
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.mapping.nameOrId
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.mapping.toBannedUserListItem
import org.futo.circles.mapping.toGroupMemberListItem
import org.futo.circles.mapping.toInvitedMemberListItem
import org.futo.circles.model.BannedMemberListItem
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.InvitedMemberListItem
import org.futo.circles.model.ManageMembersHeaderListItem
import org.futo.circles.model.ManageMembersListItem
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
import javax.inject.Inject

@ViewModelScoped
class ManageMembersDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ExpandableItemsDataSource {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val type: CircleRoomTypeArg = savedStateHandle.getOrThrow("type")

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

    private fun getRoomMembersSummaryFlow(): Flow<List<RoomMemberSummary>> =
        room?.membershipService()?.getRoomMembersLive(roomMemberQueryParams())?.asFlow()
            ?: flowOf()

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
        val invitedUsers = mutableListOf<InvitedMemberListItem>()
        val bannedUsers = mutableListOf<BannedMemberListItem>()

        members.forEach { member ->
            when (member.membership) {
                Membership.INVITE -> invitedUsers.add(
                    member.toInvitedMemberListItem(
                        usersWithVisibleOptions.contains(member.userId),
                        powerLevelsContent
                    )
                )

                Membership.BAN -> bannedUsers.add(
                    member.toBannedUserListItem(powerLevelsContent)
                )

                Membership.JOIN -> currentMembers.add(
                    member.toGroupMemberListItem(
                        roleHelper.getUserRole(member.userId),
                        usersWithVisibleOptions.contains(member.userId),
                        powerLevelsContent
                    )
                )

                else -> {}
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
        if (bannedUsers.isNotEmpty()) {
            fullList.add(ManageMembersHeaderListItem(context.getString(R.string.banned_users)))
            fullList.addAll(bannedUsers)
        }
        return fullList
    }

    suspend fun removeUser(userId: String) =
        createResult { room?.membershipService()?.remove(userId) }

    suspend fun reInviteUser(userId: String) =
        createResult {
            room?.membershipService()?.remove(userId)
            room?.membershipService()?.invite(userId)
        }

    suspend fun banUser(userId: String) = createResult { room?.membershipService()?.ban(userId) }

    suspend fun unBanUser(userId: String) =
        createResult { room?.membershipService()?.unban(userId) }

    suspend fun changeAccessLevel(userId: String, levelValue: Int) = createResult {
        val content = powerLevelsContent?.setUserPowerLevel(userId, levelValue).toContent()
        room?.stateService()
            ?.sendStateEvent(EventType.STATE_ROOM_POWER_LEVELS, stateKey = "", content)
        Unit
    }
}