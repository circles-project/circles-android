package org.futo.circles.feature.room.update

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.extensions.launchBg

class UpdateRoomViewModel(
    private val dataSource: UpdateRoomDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val updateGroupResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val groupSummaryLiveData = MutableLiveData(dataSource.getRoomSummary())
    val isRoomDataChangedLiveData = MutableLiveData(false)

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun update(name: String, topic: String, isPublic: Boolean) {
        launchBg {
            updateGroupResponseLiveData.postValue(
                dataSource.updateRoom(name, topic, selectedImageLiveData.value, isPublic)
            )
        }
    }

    fun handleRoomDataUpdate(name: String, topic: String, isPublic: Boolean) {
        val isDataUpdated = dataSource.isNameChanged(name) ||
                dataSource.isTopicChanged(topic) ||
                dataSource.isPrivateSharedChanged(isPublic) ||
                selectedImageLiveData.value != null
        isRoomDataChangedLiveData.postValue(isDataUpdated)
    }

}