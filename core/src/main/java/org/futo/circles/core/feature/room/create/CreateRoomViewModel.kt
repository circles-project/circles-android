package org.futo.circles.core.feature.room.create

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.Group
import org.futo.circles.core.model.Timeline
import javax.inject.Inject

@HiltViewModel
class CreateRoomViewModel @Inject constructor(
    private val dataSource: CreateRoomDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val createRoomResponseLiveData = SingleEventLiveData<Response<String>>()
    val createRoomProgressEventLiveData = SingleEventLiveData<CreateRoomProgressStage>()

    private val createRoomProgressListener = object : CreateRoomProgressListener {
        override fun onProgressUpdated(event: CreateRoomProgressStage) {
            createRoomProgressEventLiveData.postValue(event)
        }
    }

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun createRoom(
        name: String,
        topic: String?,
        inviteIds: List<String>?,
        roomType: CircleRoomTypeArg,
        defaultUserAccessLevel: AccessLevel
    ) {
        launchBg {
            val result = createResult {
                val circlesRoom = when (roomType) {
                    CircleRoomTypeArg.Circle -> Timeline()
                    CircleRoomTypeArg.Group -> Group()
                    CircleRoomTypeArg.Photo -> Gallery()
                }
                dataSource.createRoom(
                    circlesRoom = circlesRoom,
                    iconUri = selectedImageLiveData.value,
                    name = name,
                    topic = topic,
                    inviteIds = inviteIds,
                    defaultUserPowerLevel = defaultUserAccessLevel.levelValue,
                    progressObserver = createRoomProgressListener
                )
            }
            createRoomProgressEventLiveData.postValue(CreateRoomProgressStage.Finished)
            createRoomResponseLiveData.postValue(result)
        }
    }
}