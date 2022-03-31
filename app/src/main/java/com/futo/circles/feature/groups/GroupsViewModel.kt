package com.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.core.GROUP_TYPE
import com.futo.circles.mapping.toGroupListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams


class GroupsViewModel : ViewModel() {

    val groupsLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams {
            includeType = listOf(GROUP_TYPE)
        })?.map { list -> list.map { it.toGroupListItem() } }

}
