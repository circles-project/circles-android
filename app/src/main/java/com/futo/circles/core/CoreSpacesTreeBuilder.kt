package com.futo.circles.core

import com.futo.circles.feature.create_group.data_source.CreateRoomDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoreSpacesTreeBuilder(private val createRoomDataSource: CreateRoomDataSource) {

    suspend fun createCoreSpacesTree() {
        createRoomDataSource.createSpace(TOP_LEVEL_SPACE_NAME)
        coroutineScope {
            listOf(
                async { createRoomDataSource.createSpace(CIRCLES_SPACE_NAME) },
                async { createRoomDataSource.createSpace(GROUPS_SPACE_NAME) },
                async { createRoomDataSource.createSpace(PHOTOS_SPACE_NAME) }
            ).awaitAll()
        }
    }

    companion object {
        private const val TOP_LEVEL_SPACE_NAME = "Circles"
        private const val CIRCLES_SPACE_NAME = "My Circles"
        private const val GROUPS_SPACE_NAME = "My Groups"
        private const val PHOTOS_SPACE_NAME = "My Photo Galleries"
    }
}