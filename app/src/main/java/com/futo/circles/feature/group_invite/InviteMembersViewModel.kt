package com.futo.circles.feature.group_invite

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.extensions.launchUi
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource
import com.futo.circles.model.RoomMemberListItem
import kotlinx.coroutines.flow.*

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val usersLiveData = MutableLiveData<List<RoomMemberListItem>>()

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .debounce(500)
                .distinctUntilChanged()
                .flatMapLatest { query -> dataSource.search(query) }
                .collectLatest { members ->
                    usersLiveData.postValue(members)
                    Log.d("MyLog", members.size.toString())
                }
        }
    }


}