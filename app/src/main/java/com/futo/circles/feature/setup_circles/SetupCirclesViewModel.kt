package com.futo.circles.feature.setup_circles

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.setup_circles.data_source.SetupCirclesDataSource

class SetupCirclesViewModel(
    dataSource: SetupCirclesDataSource
) : ViewModel() {

    val circlesLiveData = dataSource.circlesLiveData

    fun createCircles() {

    }

    fun addImageForCircle(id: Int?, uri: Uri) {

    }
}