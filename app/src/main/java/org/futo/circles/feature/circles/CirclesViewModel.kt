package org.futo.circles.feature.circles

import androidx.lifecycle.map
import org.futo.circles.core.rooms.RoomsViewModel
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CirclesViewModel(
    private val dataSource: CirclesDataSource
) : RoomsViewModel(dataSource) {

    override val roomsLiveData =
        MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            ?.map { list -> dataSource.filterRooms(list) }

}