package org.futo.circles.feature.photos

import androidx.lifecycle.ViewModel

class PhotosViewModel(
    dataSource: PhotosDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGalleriesLiveData()

}