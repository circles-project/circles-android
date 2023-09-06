package org.futo.circles.core.room.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.futo.circles.core.ErrorParser
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class InviteMembersViewModel @Inject constructor(
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