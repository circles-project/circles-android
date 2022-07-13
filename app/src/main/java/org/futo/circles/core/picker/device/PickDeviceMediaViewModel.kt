package org.futo.circles.core.picker.device

import androidx.lifecycle.ViewModel
import org.futo.circles.extensions.launchBg

class PickDeviceMediaViewModel(
    private val dataSource: PickDeviceMediaDataSource
) : ViewModel() {

    val mediaLiveData = dataSource.mediaLiveData

    init {
        launchBg { dataSource.fetchMedia() }
    }


}