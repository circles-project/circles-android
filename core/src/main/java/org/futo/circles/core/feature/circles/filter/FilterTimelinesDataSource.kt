package org.futo.circles.core.feature.circles.filter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.toFilterTimelinesListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

class FilterTimelinesDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val circleId: String = savedStateHandle.getOrThrow("circleId")

    private val session = MatrixSessionProvider.getSessionOrThrow()
    val circleSummaryLiveData = session.roomService().getRoomSummaryLive(circleId)
    private val selectedTimelinesIds = MutableStateFlow(getCircleFilter())

    val timelinesLiveData = combine(
        circleSummaryLiveData.asFlow(),
        selectedTimelinesIds
    ) { circle, selectedIds ->
        val children = circle.getOrNull()?.spaceChildren ?: emptyList()
        val myTimelineId = getTimelineRoomFor(circleId)?.roomId
        children.mapNotNull {
            session.getRoom(it.childRoomId)?.roomSummary()?.takeIf { summary ->
                summary.membership.isActive() && summary.roomId != myTimelineId
            }?.toFilterTimelinesListItem(isTimelineSelected(selectedIds, it.childRoomId))
        }
    }.flowOn(Dispatchers.IO).asLiveData()

    suspend fun applyFilter() = createResult {
        session.getRoom(circleId)?.roomAccountDataService()
            ?.updateAccountData(
                CIRCLE_FILTER_EVENT_TYPE,
                mapOf(TIMELINES_KEY to selectedTimelinesIds.value)
            )
    }

    fun toggleItemSelected(roomId: String) {
        val isItemSelected = isTimelineSelected(selectedTimelinesIds.value, roomId)
        selectedTimelinesIds.update { value ->
            val newSet = value.toMutableSet()
            if (isItemSelected) newSet.remove(roomId)
            else newSet.add(roomId)
            newSet
        }
    }

    fun selectAllTimelines() {
        val ids = timelinesLiveData.value?.map { it.id }?.toSet() ?: emptySet()
        selectedTimelinesIds.update { ids }
    }

    private fun isTimelineSelected(selectedIds: Set<String>, roomId: String): Boolean =
        if (selectedIds.isEmpty()) true
        else selectedIds.contains(roomId)

    private fun getCircleFilter(): Set<String> {
        val content = session.getRoom(circleId)?.roomAccountDataService()
            ?.getAccountDataEvent(CIRCLE_FILTER_EVENT_TYPE)?.content ?: return emptySet()
        return (content[TIMELINES_KEY] as? List<*>)?.map { it.toString() }?.toSet() ?: emptySet()
    }

    companion object {
        private const val CIRCLE_FILTER_EVENT_TYPE = "m.circle.filter"
        private const val TIMELINES_KEY = "timelines"
    }
}