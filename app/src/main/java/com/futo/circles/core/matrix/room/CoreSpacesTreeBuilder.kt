package com.futo.circles.core.matrix.room

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoreSpacesTreeBuilder(private val createRoomDataSource: CreateRoomDataSource) {

    suspend fun createCoreSpacesTree() {
        createRoomDataSource.createCirclesRoom(RootSpace())
        coroutineScope {
            listOf(
                async { createRoomDataSource.createCirclesRoom(CirclesSpace()) },
                async { createRoomDataSource.createCirclesRoom(GroupsSpace()) },
                async { createRoomDataSource.createCirclesRoom(PhotosSpace()) },
            ).awaitAll()
        }
    }
}