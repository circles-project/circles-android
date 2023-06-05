package org.futo.circles.feature.room.manage_members

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg

class ManageMembersViewModel(
    private val dataSource: ManageMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getManageMembersTittle())
    val groupMembersLiveData = dataSource.getRoomMembersFlow().asLiveData()

    val removeUserResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val banUserResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val changeAccessLevelLiveData = SingleEventLiveData<Response<String?>>()

    fun toggleOptionsVisibility(userId: String) {
        dataSource.toggleOptionsVisibilityFor(userId)
    }


    fun removeUser(userId: String) {
        launchBg { removeUserResultLiveData.postValue(dataSource.removeUser(userId)) }
    }

    fun banUser(userId: String) {
        launchBg { banUserResultLiveData.postValue(dataSource.banUser(userId)) }
    }

    fun unBanUser(userId: String) {
        launchBg { banUserResultLiveData.postValue(dataSource.unBanUser(userId)) }
    }

    fun changeAccessLevel(userId: String, levelValue: Int) {
        launchBg {
            changeAccessLevelLiveData.postValue(dataSource.changeAccessLevel(userId, levelValue))
        }
    }
}