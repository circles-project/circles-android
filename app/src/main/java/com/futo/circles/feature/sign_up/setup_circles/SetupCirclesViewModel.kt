package com.futo.circles.feature.sign_up.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.sign_up.setup_circles.data_source.SetupCirclesDataSource
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