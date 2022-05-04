package com.futo.circles.feature.create_room

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.model.Circle
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.model.Group
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg
import com.futo.circles.model.UserListItem

class CreateRoomViewModel(
    private val dataSource: CreateRoomDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createRoomResponseLiveData = SingleEventLiveData<Response<String>>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createRoom(name: String, topic: String, users: List<UserListItem>, isGroup: Boolean) {
        launchBg {
            val result = createResult {
                dataSource.createCirclesRoom(
                    circlesRoom = if (isGroup) Group() else Circle(),
                    iconUri = selectedImageLiveData.value,
                    name = name,
                    topic = if (isGroup) topic else null,
                    inviteIds = users.map { it.id }
                )
            }
            createRoomResponseLiveData.postValue(result)
        }
    }

}