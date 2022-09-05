package org.futo.circles.feature.room.select_users

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.*
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi
import org.futo.circles.mapping.toUserListItem
import org.futo.circles.model.InviteMemberListItem
import org.futo.circles.model.UserListItem
import org.matrix.android.sdk.api.session.user.model.User

class SelectUsersViewModel(
    private val dataSource: SelectUsersDataSource
) : ViewModel() {

    val searchUsersLiveData = MutableLiveData<List<InviteMemberListItem>>()
    val selectedUsersLiveData = dataSource.selectedUsersIdsFlow.asLiveData()

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { items -> searchUsersLiveData.postValue(items) }
        }
    }

    fun onUserSelected(userId: String) {
        dataSource.toggleUserSelect(userId)
    }

}