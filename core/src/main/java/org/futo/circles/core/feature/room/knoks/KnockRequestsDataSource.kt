package org.futo.circles.core.feature.room.knoks

import androidx.lifecycle.map
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType.STATE_ROOM_MEMBER
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import javax.inject.Inject

class KnockRequestsDataSource @Inject constructor() {

    fun getKnockRequestsListItemsLiveData(roomId: String) = getKnockRequestLiveData(roomId)?.map {
        it.map { user -> user.toKnockRequestListItem(roomId) }
    }

    fun getKnockRequestCountLiveData(roomId: String) =
        getKnockRequestLiveData(roomId)?.map { it.size }

    private fun getKnockRequestLiveData(roomId: String) =
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
            ?.getRoomMembersLive(
                roomMemberQueryParams {
                    excludeSelf = true
                    memberships = listOf(Membership.KNOCK)
                }
            )

    private fun getReasonMessage(roomId: String, userId: String) =
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.stateService()?.getStateEvents(
            setOf(STATE_ROOM_MEMBER), QueryStringValue.Contains(userId)
        )?.firstOrNull {
            it.content.toModel<RoomMemberContent>()?.membership == Membership.KNOCK
        }?.content.toModel<RoomMemberContent>()?.safeReason

    private fun RoomMemberSummary.toKnockRequestListItem(roomId: String) = KnockRequestListItem(
        requesterId = userId,
        requesterName = displayName ?: UserUtils.removeDomainSuffix(userId),
        requesterAvatarUrl = avatarUrl,
        message = getReasonMessage(roomId, userId)
    )
}