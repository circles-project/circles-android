package com.futo.circles.ui.groups.timeline.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.ui.groups.timeline.invite.data_source.InviteMembersDataSource

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())


}