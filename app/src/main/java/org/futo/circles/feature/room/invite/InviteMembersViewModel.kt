package org.futo.circles.feature.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.futo.circles.core.ErrorParser
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg

class InviteMembersViewModel(
    private val dataSource: InviteMembersDataSource
) : ViewModel() {

    val titleLiveData = MutableLiveData(dataSource.getInviteTitle())

    val inviteResultLiveData = SingleEventLiveData<Response<List<Unit?>>>()

    fun invite(usersIds: List<String>) {
        launchBg(
            CoroutineExceptionHandler { _, t ->
                inviteResultLiveData.postValue(Response.Error(ErrorParser.getErrorMessage(t)))
            }
        ) { inviteResultLiveData.postValue(dataSource.inviteUsers(this, usersIds)) }
    }

}