package org.futo.circles.feature.room.select

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.mapping.toSelectableRoomListItem
import org.futo.circles.core.model.CIRCLE_TAG
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.GALLERY_TYPE
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SelectRoomsDataSource(
    private val roomType: CircleRoomTypeArg
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    private val selectedRoomsFlow = MutableStateFlow<List<SelectableRoomListItem>>(emptyList())
    val roomsFlow = getMergedRoomsListFlow()

    private fun getMergedRoomsListFlow() =
        combine(getRoomsFlowWithType(), selectedRoomsFlow) { rooms, selectedRooms ->
            rooms.map { room -> room.toSelectableRoomListItem(selectedRooms.containsWithId(room.roomId)) }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getRoomsFlowWithType(): Flow<List<RoomSummary>> = when (roomType) {
        CircleRoomTypeArg.Circle -> getFilteredRoomsFlow { it.hasTag(CIRCLE_TAG) && it.membership == Membership.JOIN }
        CircleRoomTypeArg.Group -> getFilteredRoomsFlow { (it.roomType == GROUP_TYPE && it.membership == Membership.JOIN) }
        CircleRoomTypeArg.Photo -> getFilteredRoomsFlow { (it.roomType == GALLERY_TYPE && it.membership == Membership.JOIN) }
    }

    fun getSelectedRooms() = selectedRoomsFlow.value.filter { it.isSelected }

    fun toggleRoomSelect(circle: SelectableRoomListItem) {
        val list = selectedRoomsFlow.value.toMutableList()
        if (circle.isSelected) list.removeIf { it.id == circle.id }
        else list.add(circle.copy(isSelected = true))
        selectedRoomsFlow.value = list
    }

    private fun getFilteredRoomsFlow(filter: (summary: RoomSummary) -> Boolean) =
        session?.roomService()?.getRoomSummariesLive(roomSummaryQueryParams {
            excludeType = null
        })?.map { summaries ->
            summaries.mapNotNull { summary -> if (filter(summary)) summary else null }
        }?.asFlow() ?: emptyFlow()

    private fun List<SelectableRoomListItem>.containsWithId(id: String) =
        firstOrNull { it.id == id } != null
}