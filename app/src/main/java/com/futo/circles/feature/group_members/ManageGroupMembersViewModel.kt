package com.futo.circles.feature.group_members

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.group_members.data_source.ManageGroupMembersDataSource

class ManageGroupMembersViewModel(
    private val dataSource: ManageGroupMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getManageMembersTittle())
    val groupMembersLiveData = dataSource.getRoomMembersFlow().asLiveData()

    val removeUserResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val banUserResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val changeAccessLevelLiveData = SingleEventLiveData<Response<Unit?>>()

    fun toggleOptionsVisibility(userId: String) {
        dataSource.toggleOptionsVisibilityFor(userId)
    }


    fun removeUser(userId: String) {
        launchBg { removeUserResultLiveData.postValue(dataSource.removeUser(userId)) }
    }

    fun banUser(userId: String) {
        launchBg { banUserResultLiveData.postValue(dataSource.banUser(userId)) }
    }

    fun changeAccessLevel(userId: String, levelValue: Int) {
        launchBg {
            changeAccessLevelLiveData.postValue(dataSource.changeAccessLevel(userId, levelValue))
        }
    }
}