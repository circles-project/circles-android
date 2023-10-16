package org.futo.circles.feature.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.model.PeopleListItem
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val peopleDataSource: PeopleDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val peopleLiveData = MutableLiveData<List<PeopleListItem>>()
    val followUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val followUserRequestLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        launchBg { peopleDataSource.refreshRoomMembers() }
    }

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .flatMapLatest { query -> peopleDataSource.getPeopleList(query) }
                .collectLatest { items -> peopleLiveData.postValue(items) }
        }
    }

    fun unIgnoreUser(userId: String) {
        launchBg {
            unIgnoreUserLiveData.postValue(userOptionsDataSource.unIgnoreSender(userId))
        }
    }

    fun onFollowRequestAnswered(userId: String, accepted: Boolean) {
        launchBg {
            val result = if (accepted) peopleDataSource.acceptFollowRequest(userId)
            else peopleDataSource.declineFollowRequest(userId)
            followUserRequestLiveData.postValue(result)
        }

    }
}