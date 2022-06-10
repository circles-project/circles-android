package com.futo.circles.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg

class PeopleViewModel(
    peopleDataSource: PeopleDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    val peopleLiveData = peopleDataSource.getPeopleList().asLiveData()
    val ignoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

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
}