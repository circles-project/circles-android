package org.futo.circles.feature.photos

import androidx.lifecycle.map
import org.futo.circles.core.rooms.RoomsViewModel
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class PhotosViewModel(
    private val dataSource: PhotosDataSource
) : RoomsViewModel(dataSource) {

    override val roomsLiveData =
        MatrixSessionProvider.currentSession?.roomService()
            ?.getRoomSummariesLive(roomSummaryQueryParams())
            ?.map { list -> dataSource.filterRooms(list) }

}