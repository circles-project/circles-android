package org.futo.circles.feature.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.UserListItem

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val inviteResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun invite(usersIds: List<String>) {
        launchBg { inviteResultLiveData.postValue(dataSource.inviteUsers(this, usersIds)) }
    }

}