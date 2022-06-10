package com.futo.circles.feature.people

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class PeopleViewModel(dataSource: PeopleDataSource) : ViewModel() {

    val peopleLiveData = dataSource.getPeopleList().asLiveData()

    fun unIgnoreUser(id: String) {

    }

    fun ignoreUser(userId: String) {


    }
}