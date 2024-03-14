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
    ) { _, selectedIds ->
        getAllTimelinesIds().mapNotNull {
            session.getRoom(it)?.roomSummary()?.toFilterTimelinesListItem(selectedIds.contains(it))
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
        val isItemSelected = selectedTimelinesIds.value.contains(roomId)
        selectedTimelinesIds.update { value ->
            val newSet = value.toMutableSet()
            if (isItemSelected) newSet.remove(roomId)
            else newSet.add(roomId)
            newSet
        }
    }

    fun selectAllTimelines() {
        selectedTimelinesIds.update { getAllTimelinesIds() }
    }

    private fun getCircleFilter(): Set<String> {
        val content = session.getRoom(circleId)?.roomAccountDataService()
            ?.getAccountDataEvent(CIRCLE_FILTER_EVENT_TYPE)?.content ?: return getAllTimelinesIds()
        return (content[TIMELINES_KEY] as? List<*>)?.map { it.toString() }?.toSet()
            ?: getAllTimelinesIds()
    }

    private fun getAllTimelinesIds(): Set<String> {
        val children = session.getRoom(circleId)?.roomSummary()?.spaceChildren ?: emptyList()
        val myTimelineId = getTimelineRoomFor(circleId)?.roomId
        return children.mapNotNull {
            val timelineSummary =
                session.getRoom(it.childRoomId)?.roomSummary()?.takeIf { summary ->
                    summary.membership.isActive() && summary.roomId != myTimelineId
                }
            timelineSummary?.roomId
        }.toSet()
    }

    companion object {
        private const val CIRCLE_FILTER_EVENT_TYPE = "m.circle.filter"
        private const val TIMELINES_KEY = "timelines"
    }
}