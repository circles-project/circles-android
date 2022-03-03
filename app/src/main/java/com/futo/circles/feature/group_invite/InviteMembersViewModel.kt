package com.futo.circles.feature.group_invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.extensions.launchUi
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource
import com.futo.circles.model.CirclesUser
import com.futo.circles.model.InviteMemberListItem
import kotlinx.coroutines.flow.*

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val searchUsersLiveData = MutableLiveData<List<InviteMemberListItem>>()

    val selectedUsersLiveData = dataSource.selectedUsersFlow.asLiveData()

    val inviteResultLiveData = MutableLiveData<Response<Unit>>()

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { items -> searchUsersLiveData.postValue(items) }
        }
    }

    fun onUserSelected(user: CirclesUser) {
        dataSource.toggleUserSelect(user)
    }

    fun invite() {
        launchBg { inviteResultLiveData.postValue(dataSource.inviteUsers(this)) }
    }

}