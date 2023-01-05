package org.futo.circles.feature.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleSuggestionUserListItem

class PeopleViewModel(
    private val peopleDataSource: PeopleDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val peopleLiveData = MutableLiveData<List<PeopleListItem>>()
    val followUserLiveData = SingleEventLiveData<Response<Unit?>>()
    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

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

    fun followUser(user: PeopleSuggestionUserListItem) {
        launchBg {
            followUserLiveData.postValue(peopleDataSource.followUser(user))
        }
    }

    fun unIgnoreUser(id: String) {
        launchBg {
            unIgnoreUserLiveData.postValue(userOptionsDataSource.unIgnoreSender(id))
        }
    }
}