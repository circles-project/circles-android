package org.futo.circles.core.picker.device

import androidx.lifecycle.ViewModel
import org.futo.circles.extensions.launchBg

class PickDeviceMediaViewModel(
    isVideoAvailable: Boolean,
    private val dataSource: PickDeviceMediaDataSource
) : ViewModel() {

    val mediaLiveData = dataSource.getMediaLiveData(isVideoAvailable)

    init {
        launchBg { dataSource.fetchMedia() }
    }


}