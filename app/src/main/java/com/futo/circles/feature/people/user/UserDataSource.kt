package com.futo.circles.feature.people.user

import android.content.Context
import androidx.lifecycle.map
import com.futo.circles.R
import com.futo.circles.extensions.getRoomOwners
import com.futo.circles.mapping.toJoinedCircleListItem
import com.futo.circles.model.CIRCLE_TAG
import com.futo.circles.model.JoinedCircleListItem
import com.futo.circles.model.TIMELINE_TYPE
import com.futo.circles.provider.MatrixSessionProvider
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

    val userLiveData = session.userService().getUserLive(userId)

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