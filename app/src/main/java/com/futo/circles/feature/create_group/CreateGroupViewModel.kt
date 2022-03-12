package com.futo.circles.feature.create_group

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.create_group.data_source.CreateGroupDataSource
import com.futo.circles.model.UserListItem

class CreateGroupViewModel(
    private val dataSource: CreateGroupDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createGroupResponseLiveData = SingleEventLiveData<Response<Unit?>>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createGroup(name: String, topic: String, users: List<UserListItem>) {
        launchBg {
            createGroupResponseLiveData.postValue(
                dataSource.createGroup(selectedImageLiveData.value, name, topic, users)
            )
        }
    }

}