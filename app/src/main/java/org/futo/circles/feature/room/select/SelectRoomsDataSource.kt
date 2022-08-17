package org.futo.circles.feature.room.select

import androidx.lifecycle.MutableLiveData
import org.futo.circles.mapping.toSelectableRoomListItem
import org.futo.circles.model.CIRCLE_TAG
import org.futo.circles.model.SelectableRoomListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SelectRoomsDataSource {

    private val session by lazy { MatrixSessionProvider.currentSession }

    val roomsLiveData = MutableLiveData(getInitialRoomsList())

    private fun getInitialRoomsList(): List<SelectableRoomListItem> =
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
        })?.mapNotNull { summary ->
            if (summary.hasTag(CIRCLE_TAG) && summary.membership == Membership.JOIN)
                summary.toSelectableRoomListItem()
            else null
        } ?: emptyList()

    fun getSelectedRooms() = roomsLiveData.value?.filter { it.isSelected } ?: emptyList()

    fun toggleRoomSelect(circle: SelectableRoomListItem) {
        val newList = roomsLiveData.value?.toMutableList()?.map {
            if (it.id == circle.id) it.copy(isSelected = !it.isSelected) else it
        }
        roomsLiveData.postValue(newList)
    }
}