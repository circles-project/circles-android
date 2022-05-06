package com.futo.circles.feature.create_room

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.launchBg
import com.futo.circles.model.Group
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
        val inviteIds = users.map { it.id }
        launchBg {
            val result = createResult {
                if (isGroup) createGroup(name, topic, inviteIds)
                else createCircle(name, inviteIds)
            }
            createRoomResponseLiveData.postValue(result)
        }
    }

    private suspend fun createGroup(name: String, topic: String, inviteIds: List<String>) =
        dataSource.createRoom(
            circlesRoom = Group(),
            iconUri = selectedImageLiveData.value,
            name = name,
            topic = topic,
            inviteIds = inviteIds
        )

    private suspend fun createCircle(name: String, inviteIds: List<String>) =
        dataSource.createCircleWithTimeline(
            name = name,
            iconUri = selectedImageLiveData.value,
            inviteIds = inviteIds
        )
}