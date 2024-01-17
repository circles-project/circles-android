package org.futo.circles.feature.room.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.mapping.toSelectableRoomListItem
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.utils.getGalleriesLiveData
import org.futo.circles.core.utils.getGroupsLiveData
import org.futo.circles.core.utils.getSpacesLiveData
import org.futo.circles.feature.circles.CirclesDataSource
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

@ViewModelScoped
class SelectRoomsDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val circleDataSource: CirclesDataSource
) {

    private val ordinal = savedStateHandle.getOrThrow<Int>(SelectRoomsFragment.TYPE_ORDINAL)
    private val roomType: CircleRoomTypeArg =
        CircleRoomTypeArg.entries.firstOrNull { it.ordinal == ordinal }
            ?: CircleRoomTypeArg.Circle

    private val selectedRoomsFlow = MutableStateFlow<List<SelectableRoomListItem>>(emptyList())
    val roomsFlow = getMergedRoomsListFlow()

    private fun getMergedRoomsListFlow() =
        combine(getRoomsFlowWithType(), selectedRoomsFlow) { rooms, selectedRooms ->
            rooms.map { room -> room.toSelectableRoomListItem(selectedRooms.containsWithId(room.roomId)) }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getRoomsFlowWithType(): Flow<List<RoomSummary>> = when (roomType) {
        CircleRoomTypeArg.Circle -> {
            val joinedCirclesIds = circleDataSource.getJoinedCirclesIds()
            getSpacesLiveData(listOf(Membership.JOIN)).map { summaries ->
                summaries.mapNotNull { summary ->
                    if (joinedCirclesIds.contains(summary.roomId)) summary
                    else null
                }
            }.asFlow()
        }

        CircleRoomTypeArg.Group -> getGroupsLiveData(listOf(Membership.JOIN)).asFlow()
        CircleRoomTypeArg.Photo -> getGalleriesLiveData(listOf(Membership.JOIN)).asFlow()
    }

    fun getSelectedRooms() = selectedRoomsFlow.value.filter { it.isSelected }

    fun toggleRoomSelect(circle: SelectableRoomListItem) {
        val list = selectedRoomsFlow.value.toMutableList()
        if (circle.isSelected) list.removeIf { it.id == circle.id }
        else list.add(circle.copy(isSelected = true))
        selectedRoomsFlow.value = list
    }

    private fun List<SelectableRoomListItem>.containsWithId(id: String) =
        firstOrNull { it.id == id } != null
}