package com.futo.circles.feature.create_group

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.create_group.data_source.CreateGroupDataSource

class CreateGroupViewModel(
    private val dataSource: CreateGroupDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createGroupResponseLiveData = MutableLiveData<Response<Unit?>>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createGroup(name: String, topic: String) {
        launchBg {
            createGroupResponseLiveData.postValue(
                dataSource.createGroup(selectedImageLiveData.value, name, topic)
            )
        }
    }

}