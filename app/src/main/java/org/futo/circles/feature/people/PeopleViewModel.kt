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
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        launchBg { peopleDataSource.refreshRoomMembers() }
    }

    fun unIgnoreUser(id: String) {
        launchBg {
            ignoreUserLiveData.postValue(userOptionsDataSource.unIgnoreSender(id))
        }
    }

    fun ignoreUser(id: String) {
        launchBg {
            ignoreUserLiveData.postValue(userOptionsDataSource.ignoreSender(id))
        }
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
}