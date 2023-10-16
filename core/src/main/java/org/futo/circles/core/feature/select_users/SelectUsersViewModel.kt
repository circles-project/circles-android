package org.futo.circles.core.feature.select_users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.core.model.InviteMemberListItem
import org.futo.circles.core.model.UserListItem
import javax.inject.Inject

@HiltViewModel
class SelectUsersViewModel @Inject constructor(
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