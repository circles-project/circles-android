package com.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.core.matrix.room.GROUP_TYPE
import com.futo.circles.mapping.toGroupListItem
import com.futo.circles.model.GroupListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel : ViewModel() {

    val groupsLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> filterGroups(list) }


    private fun filterGroups(list: List<RoomSummary>): List<GroupListItem> {
        return list.mapNotNull { summary ->
            if (summary.roomType == GROUP_TYPE && summary.membership.isActive())
                summary.toGroupListItem()
            else null
        }
    }
}
