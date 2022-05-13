package com.futo.circles.feature.groups

import androidx.lifecycle.map
import com.futo.circles.core.rooms.RoomsViewModel
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.groups.data_source.GroupsDataSource
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class GroupsViewModel(
    private val dataSource: GroupsDataSource
) : RoomsViewModel(dataSource) {

    override val roomsLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> dataSource.filterRooms(list) }

    fun acceptGroupInvite(roomId: String) {
        launchBg { inviteResultLiveData.postValue(dataSource.acceptInvite(roomId)) }
    }
}