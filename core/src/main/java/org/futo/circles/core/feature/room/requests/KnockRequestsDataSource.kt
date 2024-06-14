package org.futo.circles.core.feature.room.requests

import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.core.extensions.isCurrentUserAbleToInvite
import org.futo.circles.core.mapping.toKnockRequestListItem
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import javax.inject.Inject

class KnockRequestsDataSource @Inject constructor() {


    fun getKnockRequestsListItemsFlow(roomId: String): Flow<List<KnockRequestListItem>> {
        val powerLevelsFlow = getRoomPowerLevelsFlow(roomId)
        val knockRequestsFlow = getKnockRequestFlow(roomId)
        return combine(powerLevelsFlow, knockRequestsFlow) { powerLevels, knockRequest ->
            if (powerLevels.isCurrentUserAbleToInvite()) {
                knockRequest.map {
                    it.toKnockRequestListItem(roomId)
                }
            } else {
                emptyList()
            }
        }
    }


    fun getKnockRequestCountFlow(roomId: String): Flow<Int> =
        getKnockRequestsListItemsFlow(roomId).map { it.size }

    private fun getRoomPowerLevelsFlow(roomId: String): Flow<PowerLevelsContent> {
        val session = MatrixSessionProvider.getSessionOrThrow()
        return session.getRoom(roomId)?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
    }


    private fun getKnockRequestFlow(roomId: String) =
        MatrixSessionProvider.currentSession?.getRoom(roomId)?.membershipService()
            ?.getRoomMembersLive(
                roomMemberQueryParams {
                    excludeSelf = true
                    memberships = listOf(Membership.KNOCK)
                }
            )?.asFlow() ?: flowOf()

}