package org.futo.circles.feature.room.create_room

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.Gallery
import org.futo.circles.model.Group
import org.futo.circles.model.UserListItem

class CreateRoomViewModel(
    private val dataSource: CreateRoomDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createRoomResponseLiveData = SingleEventLiveData<Response<String>>()

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createRoom(
        name: String,
        topic: String,
        inviteIds: List<String>?,
        roomType: CircleRoomTypeArg
    ) {
        launchBg {
            val result = createResult {
                when (roomType) {
                    CircleRoomTypeArg.Circle -> createCircle(name, inviteIds)
                    CircleRoomTypeArg.Group -> createGroup(name, topic, inviteIds)
                    CircleRoomTypeArg.Photo -> createGallery(name)
                }
            }
            createRoomResponseLiveData.postValue(result)
        }
    }

    private suspend fun createGroup(name: String, topic: String, inviteIds: List<String>?) =
        dataSource.createRoom(
            circlesRoom = Group(),
            iconUri = selectedImageLiveData.value,
            name = name,
            topic = topic,
            inviteIds = inviteIds
        )

    private suspend fun createCircle(name: String, inviteIds: List<String>?) =
        dataSource.createCircleWithTimeline(
            name = name,
            iconUri = selectedImageLiveData.value,
            inviteIds = inviteIds
        )

    private suspend fun createGallery(name: String) = dataSource.createRoom(
        circlesRoom = Gallery(),
        name = name,
        iconUri = selectedImageLiveData.value
    )
}