package org.futo.circles.feature.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.PeopleListItem

class PeopleViewModel(
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
                .debounce(500)
                .distinctUntilChanged()
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