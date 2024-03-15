package org.futo.circles.core.feature.timeline

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.circles.filter.CircleFilterAccountDataManager
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.mapping.nameOrId

abstract class BaseTimelineViewModel(
    savedStateHandle: SavedStateHandle,
    private val baseTimelineDataSource: BaseTimelineDataSource,
    private val filterAccountDataManager: CircleFilterAccountDataManager
) : ViewModel() {

    protected val roomId: String = savedStateHandle.getOrThrow("roomId")
    protected val timelineId: String? = savedStateHandle["timelineId"]

    val titleLiveData =
        baseTimelineDataSource.room.getRoomSummaryLive().map { it.getOrNull()?.nameOrId() ?: "" }

    val isFilterActiveLiveData = MutableLiveData(false)

    val timelineEventsLiveData = combine(
        baseTimelineDataSource.getTimelineEventFlow(),
        getFilterFlow()
    ) { events, selectedRoomIds ->
        val isActive = isFilterActive(selectedRoomIds)
        isFilterActiveLiveData.postValue(isActive)
        if (isActive) events.filter { selectedRoomIds.contains(it.postInfo.roomId) }
        else events
    }.flowOn(Dispatchers.IO).asLiveData()

    private fun getFilterFlow(): Flow<Set<String>> {
        timelineId ?: return MutableStateFlow(emptySet())

        return filterAccountDataManager.getCircleFilterLive(roomId)?.map { optionalEvent ->
            filterAccountDataManager.getEventContentAsSet(
                optionalEvent.getOrNull()?.content,
                roomId
            )
        }?.asFlow() ?: MutableStateFlow(emptySet())
    }

    private fun isFilterActive(selectedRoomIds: Set<String>): Boolean {
        timelineId ?: return false
        if (selectedRoomIds.isEmpty()) return false
        return selectedRoomIds.size != filterAccountDataManager.getAllTimelinesIds(roomId).size
    }


    override fun onCleared() {
        baseTimelineDataSource.clearTimeline()
        super.onCleared()
    }

    fun loadMore(): Boolean = baseTimelineDataSource.loadMore()
}