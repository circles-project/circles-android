package com.futo.circles.feature.group_invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.launchUi
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource
import com.futo.circles.model.CirclesUser
import com.futo.circles.model.InviteMemberListItem
import kotlinx.coroutines.flow.*

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val usersLiveData = MutableLiveData<List<InviteMemberListItem>>()

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { items -> usersLiveData.postValue(items) }
        }
    }

    fun onUserSelected(user: CirclesUser) {
        dataSource.toggleUserSelect(user)
    }

}