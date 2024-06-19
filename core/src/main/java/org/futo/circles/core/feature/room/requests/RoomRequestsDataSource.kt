package org.futo.circles.core.feature.room.requests

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.mapping.toRoomInviteListItem
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestHeaderItem
import org.futo.circles.core.model.RoomRequestListItem
import org.futo.circles.core.model.convertToCircleRoomType
import org.futo.circles.core.model.convertToStringRoomType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getRoomsLiveDataWithType
import org.futo.circles.core.utils.getRoomsWithType
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class RoomRequestsDataSource @Inject constructor(
    private val knockRequestsDataSource: KnockRequestsDataSource
) {

    private val loadingItemsIdsList = MutableStateFlow<Set<String>>(emptySet())
    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getRequestsFlow(inviteType: CircleRoomTypeArg, roomId: String?) =
        combine(
            loadingItemsIdsList,
            getRequestsFlowNoLoading(inviteType, roomId)
        ) { loadingIds, items ->
            items.map { item ->
                when (item) {
                    is KnockRequestListItem -> item.copy(isLoading = loadingIds.contains(item.id))
                    is RoomInviteListItem -> item.copy(isLoading = loadingIds.contains(item.id))
                    is RoomRequestHeaderItem -> item
                }
            }
        }.distinctUntilChanged()

    private fun getRequestsFlowNoLoading(inviteType: CircleRoomTypeArg, roomId: String?) = roomId?.let {
        knockRequestsDataSource.getKnockRequestsListItemsFlow(it)
    } ?: run {
        combine(
            getRoomInvitesFlow(inviteType),
            getKnockRequestFlow(inviteType)
        ) { invites, knocks ->
            withContext(Dispatchers.IO) {
                buildRequestsList(invites, knocks)
            }
        }
    }.distinctUntilChanged()

    private fun buildRequestsList(
        invites: List<RoomInviteListItem>,
        knocks: List<KnockRequestListItem>
    ): List<RoomRequestListItem> = mutableListOf<RoomRequestListItem>().apply {
        addSection(
            RoomRequestHeaderItem.invitesHeader,
            invites
        )
        addSection(
            RoomRequestHeaderItem.requestForInviteHeader,
            knocks
        )
    }


    fun unblurProfileImageFor(id: String) {
        roomIdsToUnblurProfile.update { set -> set.toMutableSet().apply { add(id) } }
    }

    fun toggleItemLoading(id: String) {
        loadingItemsIdsList.update { set ->
            set.toMutableSet().apply {
                if (this.contains(id)) remove(id)
                else add(id)
            }
        }
    }

    private fun getRoomInvitesFlow(
        inviteType: CircleRoomTypeArg
    ): Flow<List<RoomInviteListItem>> = combine(
        getRoomsLiveDataWithType(convertToStringRoomType(inviteType), listOf(Membership.INVITE))
            .asFlow(),
        MatrixSessionProvider.getSessionOrThrow().getKnownUsersFlow(),
        roomIdsToUnblurProfile
    ) { roomSummaries, knownUsers, roomIdsToUnblur ->
        val knownUsersIds = knownUsers.map { it.userId }.toSet()
        roomSummaries.map {
            it.toRoomInviteListItem(
                convertToCircleRoomType(it.roomType),
                shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
            )
        }
    }


    private fun getKnockRequestFlow(inviteType: CircleRoomTypeArg): Flow<List<KnockRequestListItem>> {
        val flows = getRoomsWithType(
            convertToStringRoomType(inviteType), listOf(Membership.JOIN)
        ).map { knockRequestsDataSource.getKnockRequestsListItemsFlow(it.roomId) }
        return combine(flows) { values -> values.toList().flatten() }
    }

    private fun shouldBlurIconFor(
        roomSummary: RoomSummary,
        knownUserIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): Boolean {
        val isKnownUser = knownUserIds.contains(roomSummary.inviterId)
        val isRoomUnbluredByUser = roomIdsToUnblur.contains(roomSummary.roomId)
        val hasIcon = roomSummary.avatarUrl.isNotEmpty()
        return !isKnownUser && !isRoomUnbluredByUser && hasIcon
    }

    private fun MutableList<RoomRequestListItem>.addSection(
        title: RoomRequestHeaderItem,
        items: List<RoomRequestListItem>
    ) {
        if (items.isNotEmpty()) {
            add(title)
            addAll(items)
        }
    }
}