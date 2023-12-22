package org.futo.circles.gallery.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    dataSource: PhotosDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGalleriesFlow().asLiveData()
}