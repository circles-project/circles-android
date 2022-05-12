package com.futo.circles.feature.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.room.invite.data_source.InviteMembersDataSource
import com.futo.circles.model.UserListItem

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val inviteResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun invite(users: List<UserListItem>) {
        launchBg { inviteResultLiveData.postValue(dataSource.inviteUsers(this, users)) }
    }

}