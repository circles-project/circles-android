package org.futo.circles.gallery.feature

import androidx.lifecycle.ViewModel
import org.futo.circles.gallery.feature.PhotosDataSource

class PhotosViewModel(
    dataSource: PhotosDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGalleriesLiveData()

}