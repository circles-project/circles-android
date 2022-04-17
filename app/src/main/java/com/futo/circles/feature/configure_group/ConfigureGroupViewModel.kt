package com.futo.circles.feature.configure_group

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.configure_group.data_source.ConfigureGroupDataSource

class ConfigureGroupViewModel(
    private val dataSource: ConfigureGroupDataSource
) : ViewModel() {

    val selectedImageLiveData = MutableLiveData<Uri>()
    val updateGroupResponseLiveData = SingleEventLiveData<Response<Unit?>>()
    val groupSummaryLiveData = MutableLiveData(dataSource.getRoomSummary())
    val isGroupDataChangedLiveData = MutableLiveData(false)

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun updateGroup(name: String, topic: String) {
        launchBg {
            updateGroupResponseLiveData.postValue(
                dataSource.updateGroup(name, topic, selectedImageLiveData.value)
            )
        }
    }

    fun handleGroupDataUpdate(name: String, topic: String) {
        val isGroupDataUpdated = dataSource.isNameChanged(name) ||
                dataSource.isTopicChanged(topic) ||
                selectedImageLiveData.value != null
        isGroupDataChangedLiveData.postValue(isGroupDataUpdated)
    }

}