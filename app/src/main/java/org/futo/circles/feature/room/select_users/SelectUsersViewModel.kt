package org.futo.circles.feature.room.select_users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.InviteMemberListItem
import org.futo.circles.model.UserListItem
import kotlinx.coroutines.flow.*

class SelectUsersViewModel(
    private val dataSource: SelectUsersDataSource
) : ViewModel() {


    val searchUsersLiveData = MutableLiveData<List<InviteMemberListItem>>()

    val selectedUsersLiveData = dataSource.selectedUsersFlow.asLiveData()


    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { items -> searchUsersLiveData.postValue(items) }
        }
    }

    fun onUserSelected(user: UserListItem) {
        dataSource.toggleUserSelect(user)
    }

}