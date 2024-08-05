package org.futo.circles.core.feature.room.select

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
import org.futo.circles.core.model.SelectRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getGalleriesLiveData
import org.futo.circles.core.utils.getGroupsLiveData
import org.futo.circles.core.utils.getTimelinesLiveData
import org.futo.circles.core.utils.getTimelinesOwnedByMeLiveData
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

@ViewModelScoped
class SelectRoomsDataSource @Inject constructor(savedStateHandle: SavedStateHandle) {

    private val ordinal = savedStateHandle.getOrThrow<Int>(SelectRoomsFragment.TYPE_ORDINAL)
    private val roomType: SelectRoomTypeArg =
        SelectRoomTypeArg.entries.firstOrNull { it.ordinal == ordinal }
            ?: SelectRoomTypeArg.CirclesJoined

    private val filterUserId: String? = savedStateHandle[SelectRoomsFragment.USER_ID]

    private val selectedRoomsFlow = MutableStateFlow<List<SelectableRoomListItem>>(emptyList())
    val roomsFlow = getMergedRoomsListFlow()

    private fun getMergedRoomsListFlow() =
        combine(getRoomsFlowWithType(), selectedRoomsFlow) { rooms, selectedRooms ->
            rooms.map { room ->
                room.toSelectableRoomListItem(selectedRooms.containsWithId(room.roomId))
            }
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    private fun getRoomsFlowWithType(): Flow<List<RoomSummary>> = when (roomType) {
        SelectRoomTypeArg.MyCircles -> getTimelinesOwnedByMeLiveData(listOf(Membership.JOIN))

        SelectRoomTypeArg.CirclesJoined -> getTimelinesLiveData(listOf(Membership.JOIN))

        SelectRoomTypeArg.MyCirclesNotJoinedByUser -> getTimelinesOwnedByMeLiveData(
            listOf(
                Membership.JOIN
            )
        ).map { summaries ->
            summaries.filter { !isUserJoinedToCircle(it) }
        }

        SelectRoomTypeArg.GroupsJoined -> getGroupsLiveData(listOf(Membership.JOIN))

        SelectRoomTypeArg.PhotosJoined -> getGalleriesLiveData(listOf(Membership.JOIN))
    }.asFlow()

    fun getSelectedRooms() = selectedRoomsFlow.value.filter { it.isSelected }

    fun toggleRoomSelect(circle: SelectableRoomListItem) {
        val list = selectedRoomsFlow.value.toMutableList()
        if (circle.isSelected) list.removeIf { it.id == circle.id }
        else list.add(circle.copy(isSelected = true))
        selectedRoomsFlow.value = list
    }

    private fun isUserJoinedToCircle(timeline: RoomSummary): Boolean {
        val member =
            MatrixSessionProvider.currentSession?.getRoom(timeline.roomId)?.membershipService()
                ?.getRoomMember(filterUserId ?: "")
        return member?.membership == Membership.JOIN
    }

    private fun List<SelectableRoomListItem>.containsWithId(id: String) =
        firstOrNull { it.id == id } != null
}