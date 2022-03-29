package com.futo.circles.feature.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.setup_circles.data_source.SetupCirclesDataSource

class SetupCirclesViewModel(
    private val dataSource: SetupCirclesDataSource
) : ViewModel() {

    val circlesLiveData = dataSource.circlesLiveData

    fun createCircles() {

    }

    fun addImageForCircle(id: Int?, uri: Uri) {
        id?.let { dataSource.addCirclesCoverImage(it, uri) }
    }
}