package org.futo.circles.feature.circles

import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import org.futo.circles.core.extensions.getKnownUsersFlow
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.feature.workspace.SpacesTreeAccountDataSource
import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import org.futo.circles.mapping.toInviteCircleListItem
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CirclesHeaderItem
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class CirclesDataSource @Inject constructor(
    private val spacesTreeAccountDataSource: SpacesTreeAccountDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource
) {

    private val roomIdsToUnblurProfile = MutableStateFlow<Set<String>>(emptySet())

    fun getCirclesFlow() = combine(
        MatrixSessionProvider.getSessionOrThrow().roomService()
            .getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            .asFlow(),
        MatrixSessionProvider.getSessionOrThrow().getKnownUsersFlow(),
        roomIdsToUnblurProfile,
        MatrixSessionProvider.getSessionOrThrow().roomService().getChangeMembershipsLive().asFlow()
    ) { roomSummaries, knownUsers, roomIdsToUnblur, _ ->
        withContext(Dispatchers.IO) {
            buildCirclesList(
                roomSummaries,
                knownUsers.map { it.userId }.toSet(),
                roomIdsToUnblur
            )
        }
    }.distinctUntilChanged()

    private fun buildCirclesList(
        list: List<RoomSummary>,
        knownUsersIds: Set<String>,
        roomIdsToUnblur: Set<String>
    ): List<CircleListItem> {
        val invites =
            list.filter { isInviteToCircleTimeline(it) }.map { it.toInviteCircleListItem(
                shouldBlurIconFor(it, knownUsersIds, roomIdsToUnblur)
            ) }

        val joinedCirclesSpaceIds = getJoinedCirclesIds()
        val joinedCircles = list.filter { isJoinedCircle(it, joinedCirclesSpaceIds) }

        val sharedCirclesTimelinesIds = sharedCircleDataSource.getSharedCirclesTimelinesIds()
        val sharedCircles = joinedCircles.filter { joinedCircle ->
            sharedCircleDataSource.isCircleShared(
                joinedCircle.roomId,
                sharedCirclesTimelinesIds
            )
        }
        val privateCircles = joinedCircles - sharedCircles.toSet()

        val displayList = mutableListOf<CircleListItem>().apply {
            addSection(CirclesHeaderItem.invitesCirclesHeader, invites)
            addSection(
                CirclesHeaderItem.sharedCirclesHeader,
                sharedCircles.map { it.toJoinedCircleListItem(true) })
            addSection(
                CirclesHeaderItem.privateCirclesHeader,
                privateCircles.map { it.toJoinedCircleListItem(false) })
        }
        return displayList
    }

    fun isJoinedCircle(summary: RoomSummary, joinedCirclesIds: List<String>): Boolean =
        joinedCirclesIds.contains(summary.roomId)

    fun getJoinedCirclesIds(): List<String> {
        val circlesSpaceId = spacesTreeAccountDataSource.getRoomIdByKey(
            CIRCLES_SPACE_ACCOUNT_DATA_KEY
        ) ?: return emptyList()
        val sharedCircleSpaceId = sharedCircleDataSource.getSharedCirclesSpaceId()
        val ids = getJoinedRoomById(circlesSpaceId)?.roomSummary()?.spaceChildren
            ?.map { it.childRoomId }
            ?.filter { it != sharedCircleSpaceId && getJoinedRoomById(it) != null }
        return ids ?: emptyList()
    }

    private fun isInviteToCircleTimeline(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.INVITE

    private fun MutableList<CircleListItem>.addSection(
        title: CirclesHeaderItem,
        items: List<CircleListItem>
    ) {
        if (items.isNotEmpty()) {
            add(title)
            addAll(items)
        }
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

    fun unblurProfileImageFor(id: String) {
        roomIdsToUnblurProfile.update { set -> set.toMutableSet().apply { add(id) } }
    }
}