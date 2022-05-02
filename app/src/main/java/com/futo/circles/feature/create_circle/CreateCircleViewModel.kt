package com.futo.circles.feature.create_circle

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.room.Circle
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg
import com.futo.circles.model.UserListItem

class CreateCircleViewModel(
    private val dataSource: CreateRoomDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createCircleResponseLiveData = SingleEventLiveData<Response<String>>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createCircle(name: String, users: List<UserListItem>) {
        launchBg {
            val result = createResult {
                dataSource.createCirclesRoom(
                    circlesRoom = Circle(),
                    iconUri = selectedImageLiveData.value,
                    name = name,
                    inviteIds = users.map { it.id }
                )
            }
            createCircleResponseLiveData.postValue(result)
        }
    }

}