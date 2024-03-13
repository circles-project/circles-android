package org.futo.circles.core.feature.circles.filter

import androidx.lifecycle.SavedStateHandle
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class FilterTimelinesDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val circleId: String = savedStateHandle.getOrThrow("circleId")

    val circleSummaryLiveData =
        MatrixSessionProvider.getSessionOrThrow().roomService().getRoomSummaryLive(circleId)

}