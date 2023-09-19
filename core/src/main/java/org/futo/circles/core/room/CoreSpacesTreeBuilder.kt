package org.futo.circles.core.room

import android.content.Context
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import org.futo.circles.core.CREATE_ROOM_DELAY
import org.futo.circles.core.R
import org.futo.circles.core.model.CirclesSpace
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.model.PeopleSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.ROOT_SPACE_TAG
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.SharedCirclesSpace
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams
import javax.inject.Inject

class CoreSpacesTreeBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createRoomDataSource: CreateRoomDataSource
) {

    val loadingLiveData = MutableLiveData<LoadingData>()

    private val coreSpaces = listOf(
        RootSpace(),
        CirclesSpace(),
        GroupsSpace(),
        PhotosSpace(),
        PeopleSpace(),
        SharedCirclesSpace()
    )

    suspend fun createCoreSpacesTree() {
        loadingLiveData.postValue(
            LoadingData(
                total = 0,
                messageId = R.string.configuring_workspace,
                isLoading = true
            )
        )
        coreSpaces.forEach {
            createRoomDataSource.createRoom(it)
            delay(CREATE_ROOM_DELAY)
        }
        createRoomDataSource.createRoom(Gallery(), context.getString(R.string.photos))
        loadingLiveData.postValue(LoadingData(isLoading = false))
    }

    fun isCirclesHierarchyCreated(): Boolean = MatrixSessionProvider.currentSession?.roomService()
        ?.getRoomSummaries(roomSummaryQueryParams { excludeType = null })
        ?.firstOrNull { summary -> summary.hasTag(ROOT_SPACE_TAG) } != null
}