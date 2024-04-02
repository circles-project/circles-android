package org.futo.circles.core.feature.circles.filter

import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.util.JsonDict
import javax.inject.Inject

class CircleFilterAccountDataManager @Inject constructor() {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    fun getCircleFilterLive(circleId: String) = session.getRoom(circleId)?.roomAccountDataService()
        ?.getLiveAccountDataEvent(CIRCLE_FILTER_EVENT_TYPE)

    fun getCircleFilter(circleId: String): Set<String> {
        val content = session.getRoom(circleId)?.roomAccountDataService()
            ?.getAccountDataEvent(CIRCLE_FILTER_EVENT_TYPE)?.content ?: return getAllTimelinesIds(
            circleId
        )
        return getEventContentAsSet(content, circleId)
    }

    fun getEventContentAsSet(content: JsonDict?, circleId: String): Set<String> {
        content ?: return getAllTimelinesIds(circleId)
        return (content[TIMELINES_KEY] as? List<*>)?.map { it.toString() }?.toSet()
            ?: getAllTimelinesIds(circleId)
    }

    fun getAllTimelinesIds(circleId: String): Set<String> {
        val children = session.getRoom(circleId)?.roomSummary()?.spaceChildren ?: emptyList()
        return children.mapNotNull {
            val timelineSummary =
                session.getRoom(it.childRoomId)?.roomSummary()?.takeIf { summary ->
                    summary.membership.isActive()
                }
            timelineSummary?.roomId
        }.toSet()
    }

    suspend fun updateFilter(circleId: String, timelineIds: Set<String>) = createResult {
        session.getRoom(circleId)?.roomAccountDataService()
            ?.updateAccountData(
                CIRCLE_FILTER_EVENT_TYPE,
                mapOf(TIMELINES_KEY to timelineIds)
            )
    }

    companion object {
        private const val CIRCLE_FILTER_EVENT_TYPE = "m.circle.filter"
        private const val TIMELINES_KEY = "timelines"
    }
}