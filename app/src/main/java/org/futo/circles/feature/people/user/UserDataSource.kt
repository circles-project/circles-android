package org.futo.circles.feature.people.user

import android.content.Context
import androidx.lifecycle.map
import org.futo.circles.R
import org.futo.circles.extensions.getRoomOwners
import org.futo.circles.mapping.toJoinedCircleListItem
import org.futo.circles.model.JoinedCircleListItem
import org.futo.circles.model.TIMELINE_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class UserDataSource(
    context: Context,
    private val userId: String
) {

    private val session by lazy {
        MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
    }

    val userLiveData = session.userService().getUserLive(userId).map {
        it.getOrNull() ?: session.getUserOrDefault(userId)
    }

    val userCirclesLiveData =
        MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            ?.map { list -> filterUsersCircles(list) }


    private fun filterUsersCircles(list: List<RoomSummary>): List<JoinedCircleListItem> {
        return list.mapNotNull { summary ->
            if (isUsersCircle(summary)) summary.toJoinedCircleListItem()
            else null
        }.sortedBy { it.membership }
    }

    private fun isUsersCircle(summary: RoomSummary) =
        summary.roomType == TIMELINE_TYPE && summary.membership == Membership.JOIN &&
                getRoomOwners(summary.roomId).map { it.userId }.contains(userId)

}