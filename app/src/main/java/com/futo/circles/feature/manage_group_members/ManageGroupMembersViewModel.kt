package com.futo.circles.feature.manage_group_members

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.manage_group_members.data_source.ManageGroupMembersDataSource

class ManageGroupMembersViewModel(
    private val dataSource: ManageGroupMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getManageMembersTittle())


}