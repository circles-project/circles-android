package org.futo.circles.core.matrix.room

import android.content.Context
import kotlinx.coroutines.delay
import org.futo.circles.R
import org.futo.circles.core.CREATE_ROOM_DELAY
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CoreSpacesTreeBuilder(
    private val context: Context,
    private val createRoomDataSource: CreateRoomDataSource
) {

    private val coreSpaces = listOf(
        RootSpace(), CirclesSpace(), GroupsSpace(), PhotosSpace()
    )

    suspend fun createCoreSpacesTree() {
        coreSpaces.forEach {
            createRoomDataSource.createRoom(it)
            delay(CREATE_ROOM_DELAY)
        }
        createRoomDataSource.createRoom(Gallery(), context.getString(R.string.photos))
    }

    fun isCirclesHierarchyCreated(): Boolean = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
        ?.firstOrNull { summary -> summary.hasTag(ROOT_SPACE_TAG) } != null

}