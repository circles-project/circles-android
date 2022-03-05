package com.futo.circles.feature.create_group

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.create_group.data_source.CreateGroupDataSource

class CreateGroupViewModel(
    private val dataSource: CreateGroupDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

}