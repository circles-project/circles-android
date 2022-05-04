package com.futo.circles.core.matrix.room

import com.futo.circles.model.CirclesSpace
import com.futo.circles.model.GroupsSpace
import com.futo.circles.model.PhotosSpace
import com.futo.circles.model.RootSpace
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoreSpacesTreeBuilder(private val createRoomDataSource: CreateRoomDataSource) {

    suspend fun createCoreSpacesTree() {
        createRoomDataSource.createRoom(RootSpace())
        coroutineScope {
            listOf(
                async { createRoomDataSource.createRoom(CirclesSpace()) },
                async { createRoomDataSource.createRoom(GroupsSpace()) },
                async { createRoomDataSource.createRoom(PhotosSpace()) },
            ).awaitAll()
        }
    }
}