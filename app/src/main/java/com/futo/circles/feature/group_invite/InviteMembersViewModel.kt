package com.futo.circles.feature.group_invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())


}