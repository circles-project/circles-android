package org.futo.circles.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.*
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.extensions.launchUi

class PeopleViewModel(
    peopleDataSource: PeopleDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val peopleLiveData = peopleDataSource.getPeopleList().asLiveData()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

    init {
        launchBg { peopleDataSource.loadAllRoomMembersIfNeeded() }
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

    }
}