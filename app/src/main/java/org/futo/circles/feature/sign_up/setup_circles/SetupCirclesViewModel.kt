package org.futo.circles.feature.sign_up.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import org.futo.circles.core.CREATE_ROOM_DELAY
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.launchBg

class SetupCirclesViewModel(
    private val setupCirclesDataSource: SetupCirclesDataSource,
    private val createRoomDataSource: CreateRoomDataSource
) : ViewModel() {

    val circlesLiveData = setupCirclesDataSource.circlesLiveData
    val createCirclesResponseLiveData = SingleEventLiveData<Response<Unit?>>()

    fun createCircles() {
        val circlesList = circlesLiveData.value ?: return
        val lastItemIndex = circlesList.size - 1
        launchBg {
            val response = createResult {
                circlesList.forEachIndexed { i, item ->
                    createRoomDataSource.createCircleWithTimeline(
                        name = item.name,
                        iconUri = item.coverUri
                    )
                    if (i != lastItemIndex) delay(CREATE_ROOM_DELAY)
                }
            }
            createCirclesResponseLiveData.postValue(response)
        }
    }

    fun addImageForCircle(id: Int?, uri: Uri) {
        id?.let { setupCirclesDataSource.addCirclesCoverImage(it, uri) }
    }
}