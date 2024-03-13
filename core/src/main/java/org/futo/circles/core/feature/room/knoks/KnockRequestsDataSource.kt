package org.futo.circles.core.feature.room.knoks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.EventType.STATE_ROOM_MEMBER
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberContent
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import javax.inject.Inject

class KnockRequestsDataSource @Inject constructor() {

    fun getKnockRequestsListItemsLiveData(roomId: String) = getKnockRequestLiveData(roomId)?.map {
        it.map { user -> user.toKnockRequestListItem(roomId) }
    } ?: MutableLiveData(emptyList())

    fun getKnockRequestCountLiveDataForCurrentUserInRoom(roomId: String): LiveData<Int> {
        val session = MatrixSessionProvider.getSessionOrThrow()
        val powerLevelsFlow = session.getRoom(roomId)?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
        val knockRequestsFlow = getKnockRequestLiveData(roomId)?.asFlow() ?: flowOf()
        return combine(powerLevelsFlow, knockRequestsFlow) { powerLevels, knockRequest ->
            if (powerLevels.isCurrentUserAbleToInvite()) knockRequest.size
            else 0
        }.asLiveData()
    }


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