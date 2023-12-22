package org.futo.circles.feature.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import javax.inject.Inject

@HiltViewModel
class GroupsViewModel @Inject constructor(
    dataSource: GroupsDataSource
) : ViewModel() {

    val roomsLiveData = dataSource.getGroupsFlow().asLiveData()
    val inviteResultLiveData = SingleEventLiveData<Response<Unit?>>()

}