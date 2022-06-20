package org.futo.circles.core.matrix.room

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.futo.circles.R
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class CoreSpacesTreeBuilder(
    private val context: Context,
    private val createRoomDataSource: CreateRoomDataSource
) {
    private val session by lazy { MatrixSessionProvider.currentSession }

    suspend fun createCoreSpacesTree() {
        createRoomDataSource.createRoom(RootSpace())
        coroutineScope {
            listOf(
                async { createRoomDataSource.createRoom(CirclesSpace()) },
                async { createRoomDataSource.createRoom(GroupsSpace()) },
                async { createRoomDataSource.createRoom(PhotosSpace()) },
            ).awaitAll()
        }
        createRoomDataSource.createRoom(Gallery(), context.getString(R.string.photos))
    }

    fun isCirclesHierarchyCreated(): Boolean = session?.roomService()
        ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
        ?.firstOrNull { summary -> summary.hasTag(ROOT_SPACE_TAG) } != null

}