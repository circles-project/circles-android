package com.futo.circles.core.matrix

import com.futo.circles.core.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoreSpacesTreeBuilder(private val createSpaceDataSource: CreateSpaceDataSource) {

    suspend fun createCoreSpacesTree() {
        createSpaceDataSource.createSpace(ROOT_SPACE_TYPE, ROOT_SPACE_NAME)
        coroutineScope {
            listOf(
                async {
                    createSpaceDataSource.createSpace(
                        CIRCLES_SPACE_TYPE,
                        CIRCLES_SPACE_NAME,
                        parentSpaceType = ROOT_SPACE_TYPE
                    )
                },
                async {
                    createSpaceDataSource.createSpace(
                        GROUPS_SPACE_TYPE,
                        GROUPS_SPACE_NAME,
                        parentSpaceType = ROOT_SPACE_TYPE
                    )
                },
                async {
                    createSpaceDataSource.createSpace(
                        PHOTOS_SPACE_TYPE,
                        PHOTOS_SPACE_NAME,
                        parentSpaceType = ROOT_SPACE_TYPE
                    )
                }
            ).awaitAll()
        }
    }
}