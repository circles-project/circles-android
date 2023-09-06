package org.futo.circles.core.room.manage_members

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ManageMembersViewModel @Inject constructor(
    private val dataSource: ManageMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getManageMembersTittle())
    val groupMembersLiveData = dataSource.getRoomMembersFlow().asLiveData()

    val responseLiveData = SingleEventLiveData<Response<Unit?>>()

    fun toggleOptionsVisibility(userId: String) {
        dataSource.toggleOptionsVisibilityFor(userId)
    }


    fun removeUser(userId: String) {
        launchBg { responseLiveData.postValue(dataSource.removeUser(userId)) }
    }

    fun banUser(userId: String) {
        launchBg { responseLiveData.postValue(dataSource.banUser(userId)) }
    }

    fun unBanUser(userId: String) {
        launchBg { responseLiveData.postValue(dataSource.unBanUser(userId)) }
    }

    fun changeAccessLevel(userId: String, levelValue: Int) {
        launchBg {
            responseLiveData.postValue(dataSource.changeAccessLevel(userId, levelValue))
        }
    }

    fun resendInvitation(userId: String) {
        launchBg {
            responseLiveData.postValue(dataSource.reInviteUser(userId))
        }
    }
}