package org.futo.circles.core.utils

import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomType
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

const val roomType = "m.room"
const val spaceType = RoomType.SPACE

fun getGroupsLiveData(membershipFilter: List<Membership> = Membership.activeMemberships()) =
    MatrixSessionProvider.getSessionOrThrow().roomService()
        .getRoomSummariesLive(roomSummaryQueryParams {
            memberships = membershipFilter
            includeType = listOf(GROUP_TYPE)
            excludeType = listOf(roomType, spaceType)
        })



