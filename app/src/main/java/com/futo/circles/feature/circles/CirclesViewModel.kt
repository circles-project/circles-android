package com.futo.circles.feature.circles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.futo.circles.core.matrix.room.CIRCLE_TAG
import com.futo.circles.mapping.toCircleListItem
import com.futo.circles.model.CircleListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.spaceSummaryQueryParams

class CirclesViewModel : ViewModel() {

    val circlesLiveData =
        MatrixSessionProvider.currentSession?.getRoomSummariesLive(spaceSummaryQueryParams())
            ?.map { list -> filterCircles(list) }


    private fun filterCircles(list: List<RoomSummary>): List<CircleListItem> {
        return list.mapNotNull { summary ->
            if (summary.hasTag(CIRCLE_TAG)) summary.toCircleListItem() else null
        }
    }
}