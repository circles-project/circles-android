package org.futo.circles.feature.room.select_users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.model.InviteMemberListItem
import org.futo.circles.model.UserListItem

class SelectUsersViewModel(
    private val dataSource: SelectUsersDataSource
) : ViewModel() {

    val searchUsersLiveData = MutableLiveData<List<InviteMemberListItem>>()
    val selectedUsersLiveData = dataSource.selectedUsersFlow.asLiveData()

    init {
        launchBg { dataSource.refreshRoomMembers() }
    }

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { items -> searchUsersLiveData.postValue(items) }
        }
    }

    fun onUserSelected(user: UserListItem) {
        launchBg { dataSource.toggleUserSelect(user) }
    }

}