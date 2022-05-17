package com.futo.circles.feature.circles

import androidx.lifecycle.map
import com.futo.circles.core.rooms.RoomsViewModel
import com.futo.circles.feature.circles.data_source.CirclesDataSource
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CirclesViewModel(
    private val dataSource: CirclesDataSource
) : RoomsViewModel(dataSource) {

    override val roomsLiveData =
        MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummariesLive(roomSummaryQueryParams { excludeType = null })
            ?.map { list -> dataSource.filterRooms(list) }

}