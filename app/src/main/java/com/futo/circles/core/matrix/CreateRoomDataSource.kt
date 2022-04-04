package com.futo.circles.core.matrix

import com.futo.circles.BuildConfig
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

abstract class CreateRoomDataSource {

    protected val session by lazy { MatrixSessionProvider.currentSession }

    protected suspend fun setRelations(childId: String, parentType: String) {
        val parentId = findIdForType(parentType) ?: return
        val spaceService = session?.spaceService()
        val via = listOf(getHomeServerDomain())

        spaceService?.let {
            it.setSpaceParent(childId, parentId, true, via)
            it.getSpace(parentId)?.addChildren(childId, via, null)
        }

    }

    private fun findIdForType(type: String): String? =
        session?.getRoomSummaries(roomSummaryQueryParams {
            includeType = listOf(type)
            excludeType = null
        })?.firstOrNull()?.roomId

    private fun getHomeServerDomain() =
        BuildConfig.MATRIX_HOME_SERVER_URL
            .substringAfter("//").replace("/", "")
}
