package com.futo.circles.core.matrix.room

import android.content.Context
import com.futo.circles.R
import com.futo.circles.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoreSpacesTreeBuilder(
    private val context: Context,
    private val createRoomDataSource: CreateRoomDataSource
) {

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
}