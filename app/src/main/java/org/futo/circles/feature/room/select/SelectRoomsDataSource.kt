package org.futo.circles.feature.room.select

import androidx.lifecycle.MutableLiveData
import org.futo.circles.mapping.toSelectableRoomListItem
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SelectRoomsDataSource(
    private val roomType: CircleRoomTypeArg
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    val roomsLiveData = MutableLiveData(getInitialRoomsList())

    private fun getInitialRoomsList(): List<SelectableRoomListItem> = when (roomType) {
        CircleRoomTypeArg.Circle -> getRooms { it.hasTag(CIRCLE_TAG) && it.membership == Membership.JOIN }
        CircleRoomTypeArg.Group -> getRooms { (it.roomType == GROUP_TYPE && it.membership == Membership.JOIN) }
        CircleRoomTypeArg.Photo -> getRooms { (it.roomType == GALLERY_TYPE && it.membership == Membership.JOIN) }
    }

    fun getSelectedRooms() = roomsLiveData.value?.filter { it.isSelected } ?: emptyList()

    fun toggleRoomSelect(circle: SelectableRoomListItem) {
        val newList = roomsLiveData.value?.toMutableList()?.map {
            if (it.id == circle.id) it.copy(isSelected = !it.isSelected) else it
        }
        roomsLiveData.postValue(newList)
    }

    private fun getRooms(filter: (summary: RoomSummary) -> Boolean) =
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
        })?.mapNotNull { summary ->
            if (filter(summary)) summary.toSelectableRoomListItem()
            else null
        } ?: emptyList()

}