package org.futo.circles.gallery.feature

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    dataSource: PhotosDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGalleriesLiveData()

}