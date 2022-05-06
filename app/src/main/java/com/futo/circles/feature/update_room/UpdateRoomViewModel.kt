package com.futo.circles.feature.update_room

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.update_room.data_source.UpdateRoomDataSource

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

    fun update(name: String, topic: String) {
        launchBg {
            updateGroupResponseLiveData.postValue(
                dataSource.updateRoom(name, topic, selectedImageLiveData.value)
            )
        }
    }

    fun handleRoomDataUpdate(name: String, topic: String) {
        val isDataUpdated = dataSource.isNameChanged(name) ||
                dataSource.isTopicChanged(topic) ||
                selectedImageLiveData.value != null
        isRoomDataChangedLiveData.postValue(isDataUpdated)
    }

}