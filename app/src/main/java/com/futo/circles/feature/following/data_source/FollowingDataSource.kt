package com.futo.circles.feature.following.data_source

import android.content.Context
import androidx.lifecycle.map
import com.futo.circles.R
import com.futo.circles.mapping.toFollowingListItem
import com.futo.circles.provider.MatrixSessionProvider

class FollowingDataSource(
    roomId: String,
    context: Context
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    private val room = session.getRoom(roomId) ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    val roomsLiveData = room.getRoomSummaryLive().map {
        val children = it.getOrNull()?.spaceChildren ?: emptyList()
        children.mapNotNull {
            session.getRoom(it.childRoomId)?.roomSummary()?.toFollowingListItem()
        }
    }
}