package org.futo.circles.core.feature.room.knoks

import androidx.lifecycle.map
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import javax.inject.Inject

class KnockRequestsDataSource @Inject constructor() {

    fun getKnockRequestsListItemsLiveData(roomId: String) = getKnockRequestLiveData(roomId)?.map {
        it.map { user -> user.toKnockRequestListItem() }
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

    private fun RoomMemberSummary.toKnockRequestListItem() = KnockRequestListItem(
        requesterId = userId,
        requesterName = displayName ?: UserUtils.removeDomainSuffix(userId),
        requesterAvatarUrl = avatarUrl
    )
}