package com.futo.circles.feature.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg
import com.futo.circles.core.matrix.CreateRoomDataSource
import com.futo.circles.core.matrix.CreateSpaceDataSource
import com.futo.circles.feature.setup_circles.data_source.SetupCirclesDataSource
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class SetupCirclesViewModel(
    private val setupCirclesDataSource: SetupCirclesDataSource,
    private val createSpaceDataSource: CreateSpaceDataSource
) : ViewModel() {

    val circlesLiveData = setupCirclesDataSource.circlesLiveData
    val createCirclesResponseLiveData = SingleEventLiveData<Response<List<String>?>>()

    fun createCircles() {
        launchBg {
            val response = createResult {
                circlesLiveData.value?.map {
                    async { createSpaceDataSource.createCircle(it.name, it.coverUri) }
                }?.awaitAll()
            }
            createCirclesResponseLiveData.postValue(response)
        }
    }

    fun addImageForCircle(id: Int?, uri: Uri) {
        id?.let { setupCirclesDataSource.addCirclesCoverImage(it, uri) }
    }
}