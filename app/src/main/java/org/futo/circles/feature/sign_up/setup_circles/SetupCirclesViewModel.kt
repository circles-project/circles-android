package org.futo.circles.feature.sign_up.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.launchBg
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class SetupCirclesViewModel(
    private val setupCirclesDataSource: SetupCirclesDataSource,
    private val createRoomDataSource: CreateRoomDataSource
) : ViewModel() {

    val circlesLiveData = setupCirclesDataSource.circlesLiveData
    val createCirclesResponseLiveData = SingleEventLiveData<Response<List<String>?>>()

    fun createCircles() {
        launchBg {
            val response = createResult {
                circlesLiveData.value?.map {
                    async {
                        createRoomDataSource.createCircleWithTimeline(
                            name = it.name,
                            iconUri = it.coverUri
                        )
                    }
                }?.awaitAll()
            }
            createCirclesResponseLiveData.postValue(response)
        }
    }

    fun addImageForCircle(id: Int?, uri: Uri) {
        id?.let { setupCirclesDataSource.addCirclesCoverImage(it, uri) }
    }
}