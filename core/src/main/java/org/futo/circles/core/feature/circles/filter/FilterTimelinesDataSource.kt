package org.futo.circles.core.feature.circles.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.toFilterTimelinesListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

class FilterTimelinesDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val circleFilterAccountDataManager: CircleFilterAccountDataManager
) {

    private val circleId: String = savedStateHandle.getOrThrow("circleId")

    private val session = MatrixSessionProvider.getSessionOrThrow()
    val circleSummaryLiveData = session.roomService().getRoomSummaryLive(circleId)
    private val selectedTimelinesIds =
        MutableStateFlow(circleFilterAccountDataManager.getCircleFilter(circleId))

    val timelinesLiveData = combine(
        circleSummaryLiveData.asFlow(),
        selectedTimelinesIds
    ) { _, selectedIds ->
        circleFilterAccountDataManager.getAllTimelinesIds(circleId).mapNotNull {
            session.getRoom(it)?.roomSummary()?.toFilterTimelinesListItem(selectedIds.contains(it))
        }
    }.flowOn(Dispatchers.IO).asLiveData()

    suspend fun applyFilter() =
        circleFilterAccountDataManager.updateFilter(circleId, selectedTimelinesIds.value)

    fun toggleItemSelected(roomId: String) {
        val isItemSelected = selectedTimelinesIds.value.contains(roomId)
        selectedTimelinesIds.update { value ->
            val newSet = value.toMutableSet()
            if (isItemSelected) newSet.remove(roomId)
            else newSet.add(roomId)
            newSet
        }
    }

    fun selectAllTimelines() {
        selectedTimelinesIds.update { circleFilterAccountDataManager.getAllTimelinesIds(circleId) }
    }

}